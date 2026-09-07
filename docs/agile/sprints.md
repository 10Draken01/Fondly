# Planificación de Sprints

Sprints de 1 semana (ajustable según tu disponibilidad real, ya que eres el único desarrollador). Cada sprint indica objetivo, historias incluidas, orden de implementación interno, dependencias y resultado esperado.

---

## Sprint 0 — Preparación del entorno (ya completado)

**Objetivo**: Tener el proyecto creado, corriendo en un dispositivo real, y con Git/GitHub configurado.

**Resultado**: ✅ Completado (Fase 1). Proyecto `Fondly` corriendo, repositorio en GitHub, documentación base (Fase 2) ya generada.

---

## Sprint 1 — Fundamentos de saldos

**Sprint Goal**: Al finalizar, el usuario podrá crear, ver, editar y jerarquizar saldos, validado con un dashboard funcional mostrando saldos reales creados por el usuario.

**Historias incluidas**: US-001, US-002, US-004, US-012

**Orden de implementación**:
1. Entidades de dominio (`Balance`) y tabla Room correspondiente.
2. `BalanceRepository` + `CreateBalanceUseCase`, `UpdateBalanceUseCase`.
3. Validación de jerarquía (BR-001, BR-002).
4. Pantalla de listado (dashboard básico) + pantalla de creación/edición.

**Dependencias**: Ninguna (primer sprint de implementación real).

**Resultado esperado**: App funcional donde se pueden crear saldos padre e hijos, y verlos listados con su disponible/objetivo.

---

## Sprint 2 — Movimiento de dinero

**Sprint Goal**: El usuario podrá registrar ingresos y gastos, viendo el disponible actualizarse correctamente en cascada.

**Historias incluidas**: US-005, US-006

**Orden de implementación**:
1. Entidad `Transaction` y tabla Room.
2. `TransactionRepository` + `RegisterTransactionUseCase` (con lógica de cascada a ancestros — BR-005).
3. Validación de sobregiro (BR-004).
4. Pantallas de registro de ingreso y gasto, con selector de saldo.

**Dependencias**: Sprint 1 (requiere `Balance` ya funcional).

**Resultado esperado**: Registrar un gasto en un saldo hijo reduce correctamente su disponible y el de su padre, sin duplicar el descuento.

---

## Sprint 3 — Automatización crítica

**Sprint Goal**: Los ingresos/gastos recurrentes y las renovaciones de saldo funcionan automáticamente, incluso con la app cerrada varios días.

**Historias incluidas**: US-010, US-011 (con sus prerequisitos técnicos de `RecurringTransactionRule` aunque US-008/US-009 de UI se hagan en el Sprint 4 — aquí se construye el motor de ejecución)

**Orden de implementación**:
1. Entidad `RecurringTransactionRule` y tabla Room.
2. `WorkManager` configurado para evaluación periódica.
3. Lógica de idempotencia (`lastExecutedDate`) para evitar duplicados (BR-009).
4. Lógica de renovación de saldos (`RenewBalanceUseCase`) con las 3 estrategias de rollover.
5. Manejo de reinicio de dispositivo, cambios de fecha/hora/zona horaria (RNF-014).

**Dependencias**: Sprint 2 (usa `RegisterTransactionUseCase`).

**Resultado esperado**: Crear manualmente (vía base de datos de prueba o pantalla mínima) una regla recurrente y comprobar que se ejecuta sola en su fecha, y que un saldo semanal se renueva solo.

---

## Sprint 4 — Refinamiento de saldos + UI de recurrencias

**Sprint Goal**: El usuario puede gestionar sus recurrencias desde la UI, y los saldos periódicos con hijos se reajustan correctamente ante gastos del padre.

**Historias incluidas**: US-003, US-007, US-008, US-009

**Orden de implementación**:
1. Pantallas de creación/listado de ingresos y gastos recurrentes (consumiendo el motor del Sprint 3).
2. Lógica de reajuste `EVEN` (BR-013, BR-014).
3. Flujo de desactivación/eliminación de saldos (BR-003).

**Dependencias**: Sprint 3.

**Resultado esperado**: El usuario configura su nómina y su Spotify desde la app; un gasto grande en el saldo semanal reajusta automáticamente el límite diario restante.

---

## Sprint 5 — Consultas e historial

**Sprint Goal**: El usuario puede revisar su historial de transacciones y su progreso de ahorro.

**Historias incluidas**: US-013, US-014

**Orden de implementación**:
1. Consultas filtradas en `TransactionRepository` (por saldo, fecha, tipo).
2. Pantalla de historial con filtros.
3. Agregación mensual de ahorro (consulta sobre `Balance` tipo `SAVINGS`).
4. Pantalla de historial de ahorro.

**Dependencias**: Sprint 2 (datos), Sprint 3 (renovaciones que generan ahorro automático).

**Resultado esperado**: El usuario ve su historial completo y cuánto ha ahorrado mes a mes.

---

## Sprint 6 — Personalización y notificaciones

**Sprint Goal**: El usuario puede personalizar tema e idioma, y recibe alertas de presupuesto.

**Historias incluidas**: US-015, US-016, US-017

**Orden de implementación**:
1. Pantalla de Configuración base.
2. Selector de tema (`AppCompatDelegate.setDefaultNightMode`).
3. Selector de idioma (`AppCompatDelegate.setApplicationLocales`) + extracción de strings a `values-en/` (si el idioma base fue español).
4. Notificaciones locales configurables (umbral por saldo).

**Dependencias**: Ninguna técnica fuerte, pero lógicamente posterior para no reescribir strings sobre la marcha durante sprints anteriores — de hecho, **RNF-010/RNF-011 aplican desde el Sprint 1**, así que la extracción a `strings.xml` debe hacerse desde el principio; este sprint solo agrega el *selector* de idioma, no la preparación de recursos (eso es continuo desde el Sprint 1).

**Resultado esperado**: App usable en Español e Inglés, con tema personalizable y alertas funcionando.

---

## Sprint 7 — Resumen mensual y pulido general

**Sprint Goal**: Cerrar el MVP con la vista de resumen mensual y una pasada general de pulido/bugs.

**Historias incluidas**: US-018

**Orden de implementación**:
1. Agregaciones mensuales de ingresos/gastos/ahorro.
2. Pantalla de resumen con navegación entre meses.
3. Revisión general de estados vacíos, errores, y consistencia visual (RNF-008, RNF-009).

**Dependencias**: Sprints 2, 3, 5.

**Resultado esperado**: MVP completo y coherente, listo para uso diario real.

---

## Nota sobre reestimación

Esta planificación es una guía inicial, no un compromiso rígido — como soy el único desarrollador, es normal que algunos sprints se extiendan o combinen según mi disponibilidad real.