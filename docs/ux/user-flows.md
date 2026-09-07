# Flujos de Usuario Principales

Notación: `→` indica navegación o transición de pantalla/estado; `⇒` indica un efecto en el sistema (no una pantalla).

## Registrar gasto

```
Dashboard → "Registrar gasto" 
  → Formulario: producto, precio, cantidad, fecha, categoría (opcional)
  → Selector de saldo (lista de Balances activos)
  → Confirmar
  ⇒ Validar disponible (BR-004)
  ⇒ Crear Transaction(EXPENSE)
  ⇒ Propagar a cadena de ancestros (BR-005)
  ⇒ Reajustar hijos periódicos si aplica (BR-013)
  → Dashboard (saldos actualizados) + confirmación visual breve
```

## Registrar ingreso

```
Dashboard → "Registrar ingreso"
  → Formulario: nombre, cantidad, fecha, descripción (opcional)
  → Selector de saldo destino
  → Confirmar
  ⇒ Crear Transaction(INCOME)
  ⇒ Propagar a cadena de ancestros (BR-008)
  → Dashboard (saldos actualizados)
```

## Crear saldo

```
Pantalla Saldos → "+" 
  → Formulario: nombre, monto objetivo, periodicidad (opcional), padre (opcional), tipo (regular/ahorro)
  → Confirmar
  ⇒ Validar jerarquía si tiene padre (BR-002)
  ⇒ Crear Balance
  → Pantalla Saldos (nuevo saldo visible)
```

## Crear saldo hijo (apartado)

```
Detalle de Saldo (padre) → "Agregar apartado"
  → Formulario (parentBalanceId preseleccionado)
  → Opción: ¿transferir monto inicial desde el padre?
    → Si sí: ingresar monto → Confirmar
      ⇒ Crear Balance hijo
      ⇒ Crear Transaction(ALLOCATION) padre → hijo
    → Si no: Confirmar
      ⇒ Crear Balance hijo con available = 0
  → Detalle de Saldo (padre) actualizado, mostrando el nuevo hijo
```

## Crear gasto recurrente (servicio/membresía)

```
Pantalla Gastos Recurrentes → "+"
  → Formulario: nombre, precio, periodicidad, día de cobro, saldo, fecha inicio, fecha fin (opcional)
  → Confirmar
  ⇒ Crear RecurringTransactionRule(EXPENSE)
  → Pantalla Gastos Recurrentes (regla visible, próxima fecha de cobro mostrada)
```

## Renovación automática (proceso en segundo plano, sin pantalla)

```
WorkManager (disparo periódico/al abrir la app)
  ⇒ Evaluar Balances con renewalDate cumplida
  ⇒ Aplicar rolloverStrategy (RESET / ACCUMULATE / TRANSFER_TO_SAVINGS)
  ⇒ Calcular nueva renewalDate
  ⇒ (Si corresponde) Crear Transaction(ALLOCATION) hacia Balance de ahorro
  → Notificación local opcional: "Tu saldo semanal se renovó"
```

## Consulta mensual (dashboard / resumen)

```
Dashboard → "Ver resumen del mes"
  ⇒ Agregar Transactions del mes por tipo (INCOME/EXPENSE)
  ⇒ Calcular total ahorrado del mes (Transactions hacia Balances tipo SAVINGS)
  → Pantalla Resumen Mensual: ingresos totales, gastos totales, ahorro del mes
```

## Registro de ahorro

```
Escenario A — Automático por renovación:
  Renovación de saldo (rolloverStrategy = TRANSFER_TO_SAVINGS)
    ⇒ Transaction(ALLOCATION) hacia Balance tipo SAVINGS
    → Reflejado automáticamente en Pantalla Ahorro

Escenario B — Manual:
  Dashboard → "Registrar ingreso" → seleccionar Balance tipo SAVINGS como destino
    ⇒ Transaction(INCOME) directa al saldo de ahorro
    → Reflejado en Pantalla Ahorro
```