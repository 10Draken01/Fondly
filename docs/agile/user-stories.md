# Historias de Usuario — MVP

Formato: `Como [usuario], quiero [acción], para [beneficio]`. Prioridad en escala MoSCoW. Complejidad en puntos de historia (escala Fibonacci: 1, 2, 3, 5, 8).

---

### US-001 — Crear saldo

Como usuario, quiero crear un saldo con nombre y monto objetivo, para poder organizar mi dinero en categorías o presupuestos.

**Criterios de aceptación**:
- Puedo introducir nombre, monto objetivo, periodicidad (opcional), descripción (opcional).
- Puedo marcarlo como tipo Ahorro.
- El saldo aparece en el listado inmediatamente después de crearlo.
- Si dejo el nombre vacío o el monto es inválido, veo un mensaje de error claro.

**Prioridad**: Must Have
**Dependencias**: Ninguna
**Complejidad**: 3
**Referencias**: CU-001, RF-001, BR-001

---

### US-002 — Editar saldo

Como usuario, quiero editar un saldo existente, para corregir o ajustar su configuración sin tener que recrearlo.

**Criterios de aceptación**:
- Puedo modificar cualquier campo excepto el identificador.
- Los cambios se reflejan de inmediato en el listado y detalle.

**Prioridad**: Must Have
**Dependencias**: US-001
**Complejidad**: 2
**Referencias**: CU-002, RF-002

---

### US-003 — Desactivar/eliminar saldo

Como usuario, quiero eliminar o desactivar un saldo que ya no uso, para mantener mi lista de saldos limpia y relevante.

**Criterios de aceptación**:
- Si el saldo no tiene transacciones, se elimina completamente.
- Si tiene transacciones, se desactiva y deja de aparecer en el listado activo, pero su historial sigue disponible.
- No puedo eliminar un saldo que tenga hijos activos sin antes resolverlos.

**Prioridad**: Should Have
**Dependencias**: US-001
**Complejidad**: 3
**Referencias**: CU-003, RF-003, BR-003

---

### US-004 — Crear saldo hijo (apartado)

Como usuario, quiero crear un saldo hijo dentro de un saldo padre, para dividir mi dinero en apartados sin perder la relación con el total de origen.

**Criterios de aceptación**:
- Puedo crear un hijo desde el detalle del padre.
- El sistema me impide crear un hijo cuyo monto objetivo, sumado a los demás hijos activos, exceda el disponible del padre.
- Puedo, opcionalmente, transferir un monto inicial del padre al hijo en el mismo flujo.

**Prioridad**: Must Have
**Dependencias**: US-001
**Complejidad**: 5
**Referencias**: CU-004, RF-004, BR-002

---

### US-005 — Registrar ingreso

Como usuario, quiero registrar un ingreso hacia un saldo específico, para que mi dinero disponible se actualice correctamente.

**Criterios de aceptación**:
- Puedo introducir nombre, cantidad, fecha y descripción opcional.
- Puedo elegir a qué saldo se destina.
- El disponible del saldo (y de sus saldos ancestros) se actualiza inmediatamente.
- El ingreso queda visible en el historial.

**Prioridad**: Must Have
**Dependencias**: US-001
**Complejidad**: 3
**Referencias**: CU-005, RF-007, RF-008, RF-009, BR-008

---

### US-006 — Registrar gasto

Como usuario, quiero registrar un gasto eligiendo de qué saldo se descuenta, para llevar control exacto de en qué se va mi dinero.

**Criterios de aceptación**:
- Puedo introducir producto/concepto, precio, cantidad, fecha, categoría opcional.
- Puedo elegir explícitamente el saldo de origen entre todos mis saldos activos.
- El disponible se descuenta del saldo elegido y de toda su cadena de saldos padre, sin duplicarse en saldos no relacionados.
- Si el gasto excede el disponible y no tengo sobregiro habilitado, veo un error y el gasto no se registra.
- El gasto queda visible en el historial.

**Prioridad**: Must Have
**Dependencias**: US-001
**Complejidad**: 5
**Referencias**: CU-006, RF-013 a RF-016, BR-004, BR-005, BR-006

---

### US-007 — Reajuste automático de saldo diario ante gasto del padre

Como usuario, quiero que mi presupuesto diario se reajuste automáticamente si gasto directamente desde mi saldo semanal/mensual, para no quedarme con días en $0 de forma abrupta.

**Criterios de aceptación**:
- Si gasto directamente en un saldo que tiene un hijo con periodicidad propia, el límite del hijo se recalcula dividiendo el disponible restante entre los periodos que faltan hasta la renovación del padre.
- El nuevo límite se refleja inmediatamente en la pantalla del saldo hijo.

**Prioridad**: Should Have
**Dependencias**: US-004, US-006
**Complejidad**: 8
**Referencias**: CU-006, RF-006, BR-013, BR-014

---

### US-008 — Crear ingreso recurrente

Como usuario, quiero configurar un ingreso recurrente (ej. nómina), para no tener que registrarlo manualmente cada vez.

**Criterios de aceptación**:
- Puedo definir nombre, cantidad, periodicidad, día de pago, saldo destino y fechas de inicio/fin.
- La regla aparece en un listado de ingresos recurrentes con su próxima fecha de ejecución.

**Prioridad**: Should Have
**Dependencias**: US-001
**Complejidad**: 5
**Referencias**: CU-007, RF-010

---

### US-009 — Crear gasto recurrente (servicio/membresía)

Como usuario, quiero registrar mis suscripciones y servicios recurrentes, para saber cuánto dinero tengo comprometido cada mes.

**Criterios de aceptación**:
- Puedo definir nombre, precio, periodicidad, fecha de cobro, saldo de descuento y fechas de inicio/fin.
- Puedo editar el precio; el cambio solo afecta cobros futuros, no el historial.

**Prioridad**: Should Have
**Dependencias**: US-001
**Complejidad**: 5
**Referencias**: CU-008, RF-017, RF-019, BR-010

---

### US-010 — Ejecución automática de recurrencias

Como usuario, quiero que mis ingresos y gastos recurrentes se registren automáticamente en su fecha, incluso si no abro la app ese día, para no tener que recordarlo manualmente.

**Criterios de aceptación**:
- Si no abro la app varios días, al abrirla (o mediante tarea en segundo plano) se registran todas las ejecuciones pendientes, sin duplicar ni omitir ninguna.
- Cada ejecución respeta cambios de fecha/hora o zona horaria del dispositivo.

**Prioridad**: Must Have
**Dependencias**: US-008, US-009
**Complejidad**: 8
**Referencias**: CU-009, RF-011, RF-012, BR-009, RNF-014

---

### US-011 — Renovación automática de saldos

Como usuario, quiero que mis saldos periódicos se renueven automáticamente, para no tener que reiniciarlos manualmente cada semana/mes.

**Criterios de aceptación**:
- Al llegar la fecha de renovación, el disponible se ajusta según la estrategia configurada (reinicio, acumulación, o transferencia a ahorro).
- La renovación ocurre exactamente una vez por periodo.

**Prioridad**: Must Have
**Dependencias**: US-001
**Complejidad**: 8
**Referencias**: CU-010, RF-020, RF-021, BR-011, BR-012

---

### US-012 — Consultar saldos (dashboard)

Como usuario, quiero ver de un vistazo el estado de todos mis saldos, para saber cuánto dinero tengo disponible en cada categoría.

**Criterios de aceptación**:
- Veo cada saldo con formato "disponible / objetivo".
- Puedo distinguir visualmente saldos padre de sus hijos.
- Si no tengo saldos creados, veo un estado vacío que me invita a crear el primero.

**Prioridad**: Must Have
**Dependencias**: US-001
**Complejidad**: 3
**Referencias**: CU-011, RF-005

---

### US-013 — Consultar historial de transacciones

Como usuario, quiero consultar mi historial de ingresos y gastos con filtros, para revisar en qué gasté o de dónde vino mi dinero.

**Criterios de aceptación**:
- Puedo filtrar por saldo, rango de fechas y tipo (ingreso/gasto).
- El listado se ordena del más reciente al más antiguo.

**Prioridad**: Should Have
**Dependencias**: US-005, US-006
**Complejidad**: 3
**Referencias**: CU-012, RF-028

---

### US-014 — Consultar historial de ahorro

Como usuario, quiero ver cuánto he ahorrado cada mes, para medir mi progreso financiero real.

**Criterios de aceptación**:
- Veo un listado mensual con el total ahorrado por mes.
- El ahorro mostrado solo incluye dinero que fue explícitamente destinado a saldos de tipo Ahorro.

**Prioridad**: Should Have
**Dependencias**: US-001, US-011
**Complejidad**: 3
**Referencias**: CU-013, RF-022, RF-023, BR-015

---

### US-015 — Configurar notificaciones

Como usuario, quiero recibir alertas cuando mi presupuesto esté por agotarse, para ajustar mis gastos a tiempo.

**Criterios de aceptación**:
- Puedo activar/desactivar tipos de notificación.
- Puedo configurar un umbral de porcentaje por saldo.
- Recibo la notificación local correspondiente cuando se cumple la condición.

**Prioridad**: Could Have
**Dependencias**: US-001
**Complejidad**: 5
**Referencias**: CU-014, RF-024, RF-025, BR-018

---

### US-016 — Configurar tema

Como usuario, quiero elegir entre tema claro, oscuro o del sistema, para usar la app cómodamente según mis preferencias visuales.

**Criterios de aceptación**:
- El cambio se aplica de inmediato sin reiniciar la app.
- La preferencia persiste entre sesiones.

**Prioridad**: Could Have
**Dependencias**: Ninguna
**Complejidad**: 2
**Referencias**: CU-015, RF-026

---

### US-017 — Configurar idioma

Como usuario, quiero elegir entre Español e Inglés, para usar la app en el idioma que prefiera, independientemente del idioma de mi teléfono.

**Criterios de aceptación**:
- El cambio se aplica de inmediato sin reiniciar la app.
- Todos los textos visibles cambian de idioma consistentemente.
- La preferencia persiste entre sesiones.

**Prioridad**: Should Have
**Dependencias**: Ninguna
**Complejidad**: 3
**Referencias**: CU-015, RF-027, RNF-010, RNF-011

---

### US-018 — Resumen mensual

Como usuario, quiero ver un resumen de mis ingresos, gastos y ahorro del mes, para entender mi salud financiera general de un vistazo.

**Criterios de aceptación**:
- Veo el total de ingresos, gastos y ahorro del mes actual.
- Puedo navegar a meses anteriores.

**Prioridad**: Could Have
**Dependencias**: US-005, US-006, US-014
**Complejidad**: 5
**Referencias**: Flujo "Consulta mensual" en `docs/ux/user-flows.md`