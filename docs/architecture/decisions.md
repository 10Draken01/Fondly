# Architecture Decision Records (ADR) — Fondly

Este documento registra las decisiones de arquitectura y modelado más importantes del proyecto, junto con el razonamiento detrás de cada una. Cada decisión queda numerada y no se elimina aunque se reemplace por otra más adelante — si una decisión cambia, se agrega una nueva ADR que la sustituye y se referencia mutuamente.

---

## ADR-0001: Modelo financiero central — Árbol de Saldos con Transacciones en Cascada

**Estado**: Aceptada
**Fecha**: 2026-09-05

### Contexto

El dominio de la aplicación gira en torno al concepto de **Saldo** (`Balance`), y desde el inicio surgieron varias preguntas críticas sin resolver:

1. ¿Un saldo hijo representa dinero realmente transferido, dinero reservado, o solo un límite de validación sobre el padre?
2. Si un gasto se descuenta de un saldo (ej. "Gastos Diarios"), ¿debe afectar automáticamente a sus saldos relacionados (Semanal, Quincenal, Mensual, Total)?
3. ¿Cómo evitar contabilizar el mismo gasto más de una vez en distintos saldos?
4. Si se gasta dinero **directamente en un saldo padre** (ej. una compra grande pagada desde "Saldo Semanal"), ¿cómo debe reajustarse el saldo hijo periódico (ej. "Saldo Diario") para no comprometer días que ya no tienen respaldo?

### Decisión

**1. Estructura de árbol, no de grafo libre.**
Un `Balance` puede tener como máximo **un padre** (`parentBalanceId`, opcional). Esto forma jerarquías simples y predecibles, nunca relaciones circulares o múltiples padres.

**2. Una transacción, un destino explícito.**
Cada `Income` o `Expense` (modelados juntos como `Transaction` en el dominio, con un campo `type`) apunta a **exactamente un** `Balance` — el que el usuario selecciona explícitamente al registrarlo. Nunca se crean transacciones duplicadas "espejo" en otros saldos.

**3. Cascada hacia arriba (ancestros), nunca hacia los lados.**
El efecto de una transacción se propaga automáticamente hacia el saldo padre, abuelo, etc. (todos sus ancestros directos), porque un padre y su hijo representan **el mismo dinero visto en distinta granularidad o categoría**. La propagación:
- **Sí** afecta a todos los ancestros directos de la cadena.
- **No** afecta a hermanos, primos, ni ningún nodo fuera de la línea de ancestros directos.

Esto resuelve dos casos con la misma regla:
- Jerarquía categórica (`Nómina` → `Comida`, `Transporte`, `Ahorro`): gastar en `Comida` reduce `Comida` y `Nómina`, pero no `Transporte`.
- Jerarquía de periodicidad (`Diario` → `Semanal` → `Mensual` → `Total`, configurada explícitamente por el usuario como cadena de padres): gastar en `Diario` reduce automáticamente Semanal, Mensual y Total.

**Por qué no hay doble conteo**: la transacción se registra una sola vez en el ledger. El descuento en los ancestros es un valor **derivado** de esa misma transacción al recorrer la cadena hacia arriba — no es un registro adicional.

**4. Asignación inicial a un hijo = transferencia interna explícita.**
Cuando el usuario "aparta" dinero del padre hacia un hijo nuevo (ej. asignar $2,000 de Nómina a Comida), esto se registra como una transacción especial de tipo `allocation` (auditable y reversible en el historial), no como un ajuste automático invisible.

**5. Restricción de integridad**: la suma de los límites (`targetAmount`) de los saldos hijo activos de un mismo padre no puede exceder el disponible del padre. Se valida en el momento de crear o editar un saldo hijo.

**6. Reajuste automático hacia abajo (replanificación de hijos periódicos).**
Cuando se registra una transacción **directamente sobre un saldo padre** que tiene un hijo con periodicidad propia (ej. gastar una suma grande directo desde "Semanal" que tiene como hijo "Diario"), el límite del hijo debe recalcularse para reflejar la nueva realidad, evitando que queden días con saldo $0 de forma abrupta o promesas de gasto que ya no son sostenibles hasta la próxima renovación del padre.

- **Estrategia elegida para el MVP: `EVEN` (redistribución uniforme)**.
  Fórmula: `nuevoLímiteHijo = disponibleActualPadre / periodosRestantesHastaRenovaciónPadre`
  Ejemplo: padre con $500 disponibles y 7 días hasta su renovación → nuevo límite diario = $71.42/día, en vez de dejar $0 los primeros días y $100 el resto.
- El campo del modelo (`rebalanceStrategy`) queda preparado con valores `EVEN | FRONT_LOADED | CUSTOM`, pero **solo `EVEN` se implementa en el MVP**. `FRONT_LOADED` (agotamiento consecutivo) y `CUSTOM` (asignación manual día por día) quedan documentadas para una versión posterior.
- **Limitación conocida del MVP**: este reajuste automático solo se garantiza correcto cuando un saldo padre tiene **una única cadena lineal** de hijos periódicos. Si en el futuro se permiten múltiples hijos periódicos compitiendo por el mismo padre simultáneamente, existe riesgo de que el dinero se "reclame" más de una vez al redistribuir — esto queda fuera de alcance del MVP y debe resolverse antes de habilitar esa configuración.
- **Disparador**: el recálculo se ejecuta automáticamente cada vez que se registra una transacción directamente sobre un saldo que tiene al menos un hijo con periodicidad propia.

**7. Renovación de saldos — estrategia configurable por saldo (`rolloverStrategy`).**
En vez de una sola estrategia fija, cada `Balance` con periodicidad tiene un campo `rolloverStrategy` con tres opciones:
- `RESET` (**valor por defecto**): el disponible vuelve al límite configurado al llegar la renovación. El sobrante de ese periodo no se transfiere a ningún lado — importante: esto no significa que el dinero "desaparezca" del sistema, ya que si el saldo tiene un padre, el disponible del padre no se resetea junto con él (a menos que también le llegue su propia renovación).
- `ACCUMULATE`: el sobrante se suma al límite del siguiente periodo.
- `TRANSFER_TO_SAVINGS`: el sobrante se transfiere automáticamente a un `Balance` marcado como tipo `SAVINGS`.

**8. Ahorro (`Savings`) no es una entidad separada — es una clasificación.**
Cualquier `Balance` puede marcarse con `type = SAVINGS`. El historial mensual de ahorro es una agregación de las transacciones que ingresaron a saldos de tipo `SAVINGS` durante ese periodo (por ingreso directo, transferencia manual, o por `rolloverStrategy = TRANSFER_TO_SAVINGS`). Esto evita confundir "ahorro" (intención explícita de preservar dinero) con "dinero simplemente no gastado todavía".

**9. Persistencia: ledger inmutable + caché derivado.**
Cada `Transaction` es un registro inmutable (nunca se edita ni se borra físicamente; correcciones se hacen con transacciones de ajuste/reversión). El campo `available` de `Balance` es un **caché** actualizado en la misma operación de base de datos que inserta cada transacción, para lecturas rápidas en el dashboard. Existe una función de recálculo que reconstruye `available` sumando el historial completo del ledger — esto garantiza que el sistema siempre pueda reconstruir el estado financiero desde cero y detectar/corregir inconsistencias.

### Consecuencias

- El modelo de dominio requiere que `Balance` tenga una relación de auto-referencia (`parentBalanceId`), y que `Transaction` tenga siempre un único `balanceId` de destino.
- La lógica de aplicar una transacción debe recorrer la cadena de ancestros en cada operación de ingreso/gasto — esto se implementará como un caso de uso (`RegisterTransactionUseCase`) que encapsula: validar saldo suficiente (o permitir sobregiro si está habilitado), insertar la transacción, actualizar el caché `available` en toda la cadena de ancestros, y disparar el recálculo de `rebalanceStrategy` en hijos periódicos si aplica.
- Se necesita una tarea de mantenimiento/depuración (`RecalculateBalanceUseCase`) para reconstruir el caché desde el ledger cuando se sospeche inconsistencia.
- La UI de creación de saldos debe dejar clara la relación padre-hijo y advertir si la suma de hijos activos se acerca o excede el disponible del padre.

### Alternativas consideradas y descartadas

- **Transferencia física completa entre "cuentas" independientes**: descartada porque no hay múltiples cuentas bancarias reales involucradas — es una sola bolsa de dinero del usuario vista en distintas categorías/periodos.
- **Saldos hijo como límites de validación sin conexión real de dinero**: descartada porque rompería la garantía de que el dinero disponible siempre cuadra con la realidad; permitiría, por ejemplo, gastar el mismo dinero "en teoría disponible" en dos saldos hijo distintos sin que el padre se entere.
- **Cascada bidireccional (hijo y hermanos)**: descartada por generar doble conteo y complejidad innecesaria; no resuelve ningún caso de uso adicional frente a la cascada unidireccional hacia ancestros.

---

## ADR-0002: Migración a arquitectura Multi-Módulo

**Estado**: Aceptada
**Fecha**: 2026-09-07
**Sustituye parcialmente**: la sección de "Arquitectura" de este documento, que originalmente descartaba multi-módulo por sobreingeniería.

### Contexto

La decisión original (ver más arriba) fue no modularizar, por ser innecesario a la escala inicial del proyecto. Sin embargo, se decidió priorizar **escalabilidad a largo plazo** por sobre el costo de configuración adicional, y aprovechar que el proyecto aún es pequeño (el momento más barato para migrar).

### Decisión

Se adopta una estructura multi-módulo con **Convention Plugins** (vía un `build-logic` incluido) para evitar repetir configuración en cada módulo. Módulos:

- `core:domain` — **módulo JVM puro** (sin Android). Contiene `model/` y `port/`. El compilador impide, a nivel de classpath, que este módulo dependa de Room, Android o cualquier framework.
- `core:application` — **módulo JVM puro**. Contiene `usecase/`. Depende solo de `core:domain`.
- `core:infrastructure` — módulo Android Library. Implementa los Ports (Room, WorkManager). Depende de `core:domain`.
- `core:ui-common` — módulo Android Library. Tema, componentes Compose compartidos entre features.
- `feature:balances`, `feature:transactions`, `feature:recurring`, `feature:savings`, `feature:settings` — módulos Android Library, uno por área funcional cohesiva (no uno por pantalla individual, para evitar una explosión de módulos triviales). Cada uno depende de `core:domain`, `core:application`, `core:ui-common` — nunca de `core:infrastructure` ni de otro `feature:*`.
- `app` — ensambla todos los `feature:*` + `core:infrastructure`, contiene `FondlyApplication`, `MainActivity`, navegación raíz.

### Reglas de dependencia (impuestas por Gradle, no solo documentadas)

```
feature:* / app  →  core:application  →  core:domain  ←  core:infrastructure
                 →  core:ui-common
```

### Consecuencias

- El aislamiento del dominio deja de depender de la disciplina del desarrollador — es imposible importar Room en `core:domain` porque ese módulo no lo tiene en su classpath.
- Se gana compilación incremental y en paralelo entre módulos.
- Se paga el costo de mantener `build-logic/` y un `build.gradle.kts` por módulo (mitigado por los Convention Plugins).
- Este ADR reemplaza el diagrama de carpetas de `architecture.md`; ese documento se actualiza para reflejar módulos Gradle reales en vez de paquetes dentro de un único módulo `app`.