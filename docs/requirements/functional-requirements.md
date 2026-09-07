# Requerimientos Funcionales

## Gestión de Saldos

- **RF-001**: El sistema debe permitir crear un `Balance` con nombre, monto objetivo, periodicidad (opcional), saldo padre (opcional), estado activo/inactivo, descripción opcional y configuración de notificaciones.
- **RF-002**: El sistema debe permitir editar cualquier campo de un `Balance` existente, excepto su identificador.
- **RF-003**: El sistema debe permitir desactivar un `Balance` en vez de eliminarlo si ya tiene transacciones asociadas, para preservar el historial. Un `Balance` sin transacciones puede eliminarse físicamente.
- **RF-004**: El sistema debe permitir asignar un `Balance` existente como hijo de otro (`parentBalanceId`), validando que la suma de los límites de los hijos activos no exceda el disponible del padre.
- **RF-005**: El sistema debe mostrar, para cada `Balance`, el formato "disponible / objetivo" (ej. "$1,500 / $2,000").
- **RF-006**: El sistema debe reajustar automáticamente el límite de un `Balance` hijo con periodicidad propia cuando se registre una transacción directa sobre su padre, según la estrategia `EVEN` (ver ADR-0001).

## Ingresos

- **RF-007**: El sistema debe permitir registrar un ingreso manual con nombre, cantidad, fecha, descripción opcional y `Balance` destino.
- **RF-008**: Todo ingreso registrado debe incrementar el disponible del `Balance` destino y de toda su cadena de ancestros.
- **RF-009**: El sistema debe mantener un historial completo y consultable de ingresos.

## Ingresos Recurrentes

- **RF-010**: El sistema debe permitir configurar un ingreso recurrente con nombre, cantidad, periodicidad, día de pago, `Balance` destino, fecha de inicio, fecha de finalización opcional y estado activo/inactivo.
- **RF-011**: El sistema debe registrar automáticamente el ingreso correspondiente cuando llegue la fecha configurada, sin intervención del usuario, incluso si la app estuvo cerrada.
- **RF-012**: El sistema no debe duplicar un ingreso recurrente ya ejecutado en el mismo periodo, incluso tras reinicios del dispositivo o retrasos del sistema operativo.

## Gastos

- **RF-013**: El sistema debe permitir registrar un gasto con nombre/concepto, precio, cantidad, fecha, descripción opcional, categoría opcional y `Balance` del cual se descuenta.
- **RF-014**: El usuario debe poder seleccionar explícitamente de qué `Balance` se descuenta cada gasto, entre todos los saldos activos disponibles.
- **RF-015**: Todo gasto registrado debe descontarse del `Balance` seleccionado y propagarse a toda su cadena de ancestros, sin duplicar el descuento en saldos no relacionados.
- **RF-016**: El sistema debe mantener un historial completo y consultable de gastos.

## Servicios y Membresías Recurrentes

- **RF-017**: El sistema debe permitir registrar un gasto recurrente (servicio/membresía) con nombre, precio, periodicidad, fecha de cobro, `Balance` de descuento, fecha de inicio, fecha de finalización opcional y estado activo/inactivo.
- **RF-018**: El sistema debe registrar automáticamente el gasto correspondiente cuando llegue la fecha de cobro configurada.
- **RF-019**: El sistema debe permitir actualizar el precio de un servicio recurrente; el cambio solo debe aplicar a los cobros futuros, preservando el precio histórico en los cobros ya registrados.

## Renovación de Saldos

- **RF-020**: El sistema debe restaurar automáticamente el disponible de un `Balance` según su periodicidad configurada y su `rolloverStrategy` (`RESET`, `ACCUMULATE`, `TRANSFER_TO_SAVINGS`).
- **RF-021**: Cada renovación debe ejecutarse exactamente una vez por periodo, sin duplicarse ni omitirse por reinicios, cambios de fecha/hora o zona horaria.

## Ahorro

- **RF-022**: El sistema debe permitir marcar un `Balance` como tipo `SAVINGS`.
- **RF-023**: El sistema debe calcular y mostrar un historial mensual de ahorro, agregando las transacciones que ingresaron a saldos de tipo `SAVINGS` en cada periodo.

## Notificaciones

- **RF-024**: El sistema debe enviar notificaciones locales de advertencia cuando el disponible de un `Balance` caiga por debajo de un umbral configurable.
- **RF-025**: El sistema debe permitir al usuario configurar qué tipos de notificación desea recibir, por saldo o de forma global.

## Configuración

- **RF-026**: El sistema debe incluir una pantalla de Configuración con selector de tema (claro/oscuro/sistema).
- **RF-027**: El sistema debe incluir en Configuración un selector de idioma (Español/Inglés), aplicado mediante `AppCompatDelegate.setApplicationLocales()`, independiente del idioma del sistema operativo.

## Historial y Consultas

- **RF-028**: El sistema debe permitir consultar el historial de transacciones filtrado por `Balance`, rango de fechas y tipo (ingreso/gasto).
- **RF-029**: El sistema debe permitir reconstruir el estado financiero completo de cualquier `Balance` a partir del historial de transacciones (ver ADR-0001, punto 9).