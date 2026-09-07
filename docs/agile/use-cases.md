# Casos de Uso — MVP

Todos los casos de uso tienen como único actor al **Usuario** (no hay backend, autenticación ni actores externos). Cada uno referencia las reglas de negocio (`BR-XXX`) y requerimientos funcionales (`RF-XXX`) aplicables, definidos en `docs/requirements/`.

---

### CU-001 — Crear saldo

**Actor**: Usuario

**Precondiciones**: Ninguna (puede ser el primer saldo de la app).

**Flujo principal**:
1. El usuario accede a la opción "Crear saldo".
2. Ingresa nombre, monto objetivo, periodicidad (opcional), descripción (opcional).
3. Opcionalmente selecciona un saldo padre existente.
4. Opcionalmente marca el saldo como tipo Ahorro.
5. Confirma la creación.
6. El sistema valida los datos y crea el `Balance` con `available = 0` (o igual a `targetAmount`, según se defina en el flujo de UX — ver `user-flows.md`).

**Flujos alternativos**:
- 3a. Si selecciona un saldo padre, el sistema valida que la suma de hijos activos + este nuevo no exceda el disponible del padre (BR-002). Si excede, se muestra error y no se crea.
- 2a. Si el nombre está vacío o el monto es inválido (negativo o no numérico), se muestra error de validación y no se crea.

**Reglas de negocio**: BR-001, BR-002.

**Resultado**: Nuevo `Balance` creado y visible en el listado de saldos.

---

### CU-002 — Editar saldo

**Actor**: Usuario

**Precondiciones**: Existe al menos un `Balance`.

**Flujo principal**:
1. El usuario selecciona un saldo existente.
2. Accede a "Editar".
3. Modifica los campos deseados (nombre, monto objetivo, periodicidad, padre, estrategias, etc.).
4. Confirma.
5. El sistema valida y actualiza el `Balance`.

**Flujos alternativos**:
- 3a. Si cambia el saldo padre, se revalida BR-002 con la nueva jerarquía.
- 3b. Si el cambio de padre generaría una relación circular, se rechaza (BR-001).

**Reglas de negocio**: BR-001, BR-002.

**Resultado**: `Balance` actualizado.

---

### CU-003 — Desactivar/eliminar saldo

**Actor**: Usuario

**Precondiciones**: Existe el saldo a eliminar.

**Flujo principal**:
1. El usuario selecciona un saldo y elige "Eliminar".
2. El sistema verifica si tiene transacciones asociadas.
3. Si no tiene transacciones, se elimina físicamente.
4. Si tiene transacciones, el sistema informa que se desactivará en su lugar y pide confirmación.
5. El usuario confirma.
6. El sistema marca `isActive = false`.

**Flujos alternativos**:
- 2a. Si el saldo tiene hijos activos, se debe informar al usuario y bloquear la eliminación/desactivación hasta que reasigne o desactive los hijos primero.

**Reglas de negocio**: BR-003.

**Resultado**: Saldo eliminado o desactivado, según corresponda; historial preservado.

---

### CU-004 — Crear saldo hijo (apartado)

**Actor**: Usuario

**Flujo principal**: Idéntico a CU-001, pero iniciado desde el contexto de un saldo padre específico (ej. botón "Agregar apartado" dentro del detalle de un saldo), preseleccionando el campo `parentBalanceId`.

**Flujo alternativo**:
- 1a. El usuario puede optar por registrar una transferencia inicial (`ALLOCATION`) desde el padre al nuevo hijo, en el mismo flujo (ver ADR-0001, punto 4).

**Reglas de negocio**: BR-001, BR-002.

**Resultado**: Nuevo `Balance` hijo creado, vinculado al padre.

---

### CU-005 — Registrar ingreso

**Actor**: Usuario

**Precondiciones**: Existe al menos un `Balance` activo.

**Flujo principal**:
1. El usuario accede a "Registrar ingreso".
2. Ingresa nombre, cantidad, fecha (por defecto hoy), descripción opcional.
3. Selecciona el `Balance` destino.
4. Confirma.
5. El sistema crea la `Transaction` tipo `INCOME`, incrementa `available` en el saldo destino y en toda su cadena de ancestros.

**Flujos alternativos**:
- 2a. Si la cantidad es inválida (≤ 0), se muestra error.

**Reglas de negocio**: BR-005, BR-008.

**Resultado**: Ingreso registrado; disponible actualizado; visible en historial (RF-009).

---

### CU-006 — Registrar gasto

**Actor**: Usuario

**Precondiciones**: Existe al menos un `Balance` activo.

**Flujo principal**:
1. El usuario accede a "Registrar gasto".
2. Ingresa producto/concepto, precio, cantidad, fecha, categoría opcional, descripción opcional.
3. Selecciona explícitamente el `Balance` del cual se descuenta (RF-014).
4. Confirma.
5. El sistema valida disponibilidad (BR-004), crea la `Transaction` tipo `EXPENSE`, decrementa `available` en el saldo seleccionado y en su cadena de ancestros (BR-005), y dispara el reajuste de hijos periódicos si el saldo afectado tiene alguno con periodicidad propia (BR-013).

**Flujos alternativos**:
- 5a. Si el gasto excede el disponible y `allowOverdraft = false`, se rechaza con mensaje de error.
- 5b. Si el gasto excede el disponible y `allowOverdraft = true`, se permite y el disponible queda en negativo.

**Reglas de negocio**: BR-004, BR-005, BR-006, BR-013.

**Resultado**: Gasto registrado; disponible actualizado en cascada; hijos periódicos reajustados si aplica.

---

### CU-007 — Crear ingreso recurrente

**Actor**: Usuario

**Flujo principal**:
1. El usuario accede a "Ingresos recurrentes → Nuevo".
2. Ingresa nombre, cantidad, periodicidad, día de pago, saldo destino, fecha de inicio, fecha de fin opcional.
3. Confirma.
4. El sistema crea el `RecurringTransactionRule` tipo `INCOME`, activo.

**Reglas de negocio**: BR-009.

**Resultado**: Regla recurrente creada; se ejecutará automáticamente en la fecha configurada (ver CU-009).

---

### CU-008 — Crear gasto recurrente (servicio/membresía)

**Actor**: Usuario

**Flujo principal**: Análogo a CU-007, con `type = EXPENSE`.

**Flujo alternativo**:
- El usuario puede editar posteriormente el monto (ej. Spotify sube de precio); el cambio solo aplica a futuras ejecuciones (BR-010).

**Reglas de negocio**: BR-009, BR-010.

**Resultado**: Regla recurrente de gasto creada.

---

### CU-009 — Ejecutar procesos automáticos (recurrencias y renovaciones)

**Actor**: Sistema (disparado por `WorkManager`, sin intervención directa del usuario)

**Precondiciones**: Existen reglas recurrentes activas o saldos con periodicidad configurada.

**Flujo principal**:
1. El sistema evalúa, en cada ejecución programada, todas las `RecurringTransactionRule` activas cuya fecha de ejecución corresponde al periodo actual y que no hayan sido ya ejecutadas (`lastExecutedDate` distinto al periodo actual).
2. Por cada una, genera la `Transaction` correspondiente y actualiza `lastExecutedDate`.
3. Evalúa todos los `Balance` cuya `renewalDate` ya se cumplió.
4. Por cada uno, aplica su `rolloverStrategy` y calcula la nueva `renewalDate`.

**Flujos alternativos**:
- 1a. Si el dispositivo estuvo apagado/sin abrir la app por varios días, el sistema debe procesar todos los periodos pendientes sin duplicar ni omitir ninguno (RNF-014).

**Reglas de negocio**: BR-009, BR-011, BR-012.

**Resultado**: Transacciones recurrentes generadas al día; saldos renovados correctamente.

---

### CU-010 — Renovar saldo (detalle del sub-proceso de CU-009)

**Actor**: Sistema

**Flujo principal**:
1. Se identifica el `Balance` cuya `renewalDate` se cumplió.
2. Se aplica la estrategia:
   - `RESET`: `available = targetAmount`.
   - `ACCUMULATE`: `available += targetAmount` (el sobrante se suma).
   - `TRANSFER_TO_SAVINGS`: el sobrante actual se transfiere (nueva `Transaction` tipo `ALLOCATION`) a un `Balance` tipo `SAVINGS` configurado, luego `available = targetAmount`.
3. Se calcula y guarda la nueva `renewalDate` según `periodicity`.

**Reglas de negocio**: BR-011, BR-012, BR-015.

**Resultado**: Saldo renovado; ahorro registrado si aplica la estrategia correspondiente.

---

### CU-011 — Consultar saldo

**Actor**: Usuario

**Flujo principal**:
1. El usuario accede al listado o detalle de saldos.
2. El sistema muestra `available / targetAmount` de cada `Balance` activo, junto con su jerarquía (padres/hijos visibles).

**Resultado**: Vista actualizada del estado financiero actual.

---

### CU-012 — Consultar historial

**Actor**: Usuario

**Flujo principal**:
1. El usuario accede a "Historial".
2. Opcionalmente filtra por saldo, rango de fechas o tipo (ingreso/gasto).
3. El sistema consulta `transactions` con los filtros aplicados y muestra el listado ordenado por fecha descendente.

**Reglas de negocio**: RF-028.

**Resultado**: Listado de transacciones filtrado.

---

### CU-013 — Consultar ahorro

**Actor**: Usuario

**Flujo principal**:
1. El usuario accede a "Ahorro".
2. El sistema agrupa por mes las `Transaction` cuyo `balanceId` corresponde a un `Balance` con `type = SAVINGS`.
3. Se muestra el total ahorrado por mes y el histórico.

**Reglas de negocio**: BR-015.

**Resultado**: Historial mensual de ahorro visible.

---

### CU-014 — Configurar notificaciones

**Actor**: Usuario

**Flujo principal**:
1. El usuario accede a Configuración → Notificaciones.
2. Activa/desactiva tipos de notificación (umbral de presupuesto, recordatorio de renovación, recordatorio de cobro recurrente).
3. Opcionalmente ajusta el `notificationThreshold` por saldo.
4. Confirma.

**Reglas de negocio**: BR-018.

**Resultado**: Preferencias de notificación guardadas; el sistema respeta la configuración al evaluar disparadores.

---

### CU-015 — Configurar tema e idioma

**Actor**: Usuario

**Flujo principal**:
1. El usuario accede a Configuración.
2. Selecciona tema (claro/oscuro/sistema).
3. Selecciona idioma (Español/Inglés).
4. El sistema aplica el cambio inmediatamente sin reiniciar la app (`AppCompatDelegate.setApplicationLocales()` para idioma; `AppCompatDelegate.setDefaultNightMode()` para tema).

**Reglas de negocio**: RF-026, RF-027.

**Resultado**: Preferencia de tema/idioma aplicada y persistida.