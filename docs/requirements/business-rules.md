# Reglas de Negocio

Consolidado de reglas explícitas del usuario y reglas adicionales detectadas durante el análisis del dominio. Todas están alineadas con [ADR-0001](../architecture/decisions.md).

## Saldos (Balance)

- **BR-001**: Un `Balance` puede tener como máximo un padre (`parentBalanceId`), nunca múltiples padres ni relaciones circulares.
- **BR-002**: La suma de los `targetAmount` de los saldos hijo **activos** de un mismo padre no puede exceder el `available` del padre en el momento de crear o activar un hijo.
- **BR-003**: Un `Balance` con transacciones asociadas no puede eliminarse físicamente; solo puede desactivarse (`isActive = false`).
- **BR-004**: No se permite registrar un gasto que exceda el disponible de un `Balance`, salvo que el usuario haya activado explícitamente la opción de sobregiro (`allowOverdraft`) para ese saldo.

## Transacciones

- **BR-005**: Toda transacción (`Transaction`) afecta únicamente al `Balance` seleccionado explícitamente por el usuario y a su cadena de ancestros directos — nunca a hermanos, primos, ni saldos no relacionados.
- **BR-006**: Un gasto nunca debe contabilizarse más de una vez, incluso cuando afecta a múltiples saldos en la cadena de ancestros (se deriva de una única transacción, no se duplica el registro).
- **BR-007**: Una transacción registrada es inmutable; las correcciones se realizan mediante transacciones de ajuste o reversión, nunca editando ni eliminando el registro original.
- **BR-008**: Un ingreso incrementa el disponible del `Balance` destino y de toda su cadena de ancestros.

## Recurrencia (Ingresos y Gastos)

- **BR-009**: Una transacción recurrente no debe ejecutarse más de una vez por periodo configurado, sin importar reinicios del dispositivo, cambios de fecha/hora o retrasos del sistema operativo en la ejecución de tareas en segundo plano.
- **BR-010**: Un cambio en el precio de un gasto recurrente (servicio/membresía) solo aplica a las ejecuciones futuras; los registros históricos conservan el precio vigente al momento de su ejecución.

## Renovación de Saldos

- **BR-011**: Cada renovación de un `Balance` periódico debe ejecutarse exactamente una vez por periodo.
- **BR-012**: El comportamiento del sobrante al renovar depende del `rolloverStrategy` configurado (`RESET`, `ACCUMULATE`, `TRANSFER_TO_SAVINGS`); por defecto es `RESET`.
- **BR-013**: Cuando se registra una transacción directamente sobre un `Balance` que tiene un hijo con periodicidad propia, el límite de ese hijo se recalcula automáticamente según la estrategia `EVEN` (ver ADR-0001), dividiendo el disponible restante del padre entre los periodos restantes hasta su próxima renovación.
- **BR-014**: El reajuste automático de hijos periódicos (BR-013) solo se garantiza correcto cuando el padre tiene una única cadena lineal de hijos periódicos. Configuraciones con múltiples hijos periódicos compitiendo por el mismo padre quedan fuera de alcance del MVP.

## Ahorro

- **BR-015**: El ahorro se define exclusivamente como transacciones que ingresan a un `Balance` de tipo `SAVINGS` — nunca como dinero simplemente no gastado en un saldo regular.

## Integridad y Auditoría

- **BR-016**: El sistema debe poder reconstruir el estado financiero completo (disponible de cualquier `Balance`) a partir del historial de transacciones, en caso de detectarse inconsistencia en el caché.
- **BR-017**: Toda operación que module el disponible de más de un `Balance` (cascada de ancestros, reajuste de hijos) debe ejecutarse de forma atómica.

## Notificaciones

- **BR-018**: Las notificaciones deben respetar la configuración de preferencias del usuario (tipos habilitados/deshabilitados); ninguna notificación configurada como deshabilitada debe enviarse.