# Modelo de Dominio

Basado en [ADR-0001](./decisions.md). Este documento define las entidades finales del dominio, deliberadamente **consolidando** varias entidades que el planteamiento inicial sugería como independientes, para evitar sobreingeniería.

## Decisiones de consolidación

El documento original sugería como posibles entidades independientes: `Balance`, `Income`, `Expense`, `RecurringIncome`, `RecurringExpense`, `Savings`, `Category`, `Transaction`, `Budget`. Análisis:

- **`Income` y `Expense` se unifican en `Transaction`** con un campo `type: INCOME | EXPENSE`. Ambos comparten exactamente los mismos campos estructurales (monto, fecha, balance destino, descripción) y la misma lógica de propagación en cascada — mantenerlos separados duplicaría código y reglas sin beneficio real.
- **`RecurringIncome` y `RecurringExpense` se unifican en `RecurringTransactionRule`**, con el mismo campo `type`. Son "plantillas" que generan `Transaction` reales al ejecutarse; la única diferencia entre ingreso y gasto recurrente es el signo del efecto, ya cubierto por `type`.
- **`Savings` no es una entidad** — es una clasificación (`type: SAVINGS`) del propio `Balance` (ver ADR-0001, punto 8).
- **`Budget` no es una entidad separada** — el concepto de "presupuesto" ya está cubierto por `Balance` (un saldo con `targetAmount` **es** un presupuesto).
- **`Category` se mantiene como campo simple opcional** (`String?` o enum liviano) dentro de `Transaction`, no como entidad con tabla y pantalla de gestión propia — para el MVP no hay necesidad de categorías anidadas, iconos personalizados, ni CRUD dedicado. Si se solicita esa granularidad más adelante, se promueve a entidad en una ADR posterior.

## Entidades finales

### `Balance`

Representa un saldo, presupuesto o apartado de dinero.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK) | Identificador único. |
| `name` | `String` | Nombre visible (ingresado por el usuario, en el idioma que él elija — es dato, no recurso de string). |
| `targetAmount` | `BigDecimal` | Monto objetivo o límite. |
| `available` | `BigDecimal` | Caché del disponible actual (ver ADR-0001, punto 9). |
| `periodicity` | `Periodicity?` enum (`DAILY, WEEKLY, BIWEEKLY, MONTHLY, YEARLY, CUSTOM, NONE`) | Periodicidad de renovación; `NONE` si no aplica. |
| `renewalDate` | `LocalDate?` | Próxima fecha de renovación (calculada). |
| `parentBalanceId` | `Long?` (FK → `Balance.id`) | Saldo padre, si aplica. |
| `type` | `BalanceType` enum (`REGULAR, SAVINGS`) | Clasificación (ver ADR-0001, punto 8). |
| `rolloverStrategy` | `RolloverStrategy` enum (`RESET, ACCUMULATE, TRANSFER_TO_SAVINGS`) | Comportamiento del sobrante al renovar. Default `RESET`. |
| `rebalanceStrategy` | `RebalanceStrategy` enum (`EVEN, FRONT_LOADED, CUSTOM`) | Estrategia de reajuste ante gasto directo del padre. Solo `EVEN` implementado en MVP. |
| `allowOverdraft` | `Boolean` | Si permite exceder el disponible. Default `false`. |
| `isActive` | `Boolean` | Estado activo/inactivo. |
| `description` | `String?` | Descripción opcional. |
| `notificationThreshold` | `Int?` | Porcentaje de umbral para notificación de advertencia (ej. 80). |
| `createdAt` | `Instant` | Fecha de creación. |

**Relaciones**: auto-referencia vía `parentBalanceId` (árbol, no grafo — ver BR-001).

**Reglas de negocio aplicables**: BR-001, BR-002, BR-003, BR-004, BR-013, BR-014.

**Estados posibles**: `Activo` / `Inactivo` (nunca "eliminado" si tiene historial — ver BR-003).

---

### `Transaction`

Representa un ingreso o gasto real, ya ejecutado.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK) | Identificador único. |
| `balanceId` | `Long` (FK → `Balance.id`) | Saldo destino/origen explícito. |
| `type` | `TransactionType` enum (`INCOME, EXPENSE, ALLOCATION, ADJUSTMENT`) | Naturaleza de la transacción. `ALLOCATION` para transferencias internas padre→hijo (ADR-0001 punto 4); `ADJUSTMENT` para correcciones (BR-007). |
| `amount` | `BigDecimal` | Monto (siempre positivo; el signo del efecto lo determina `type`). |
| `quantity` | `Int` | Cantidad de unidades (relevante para gastos de producto, ej. "2 cafés"). Default `1`. |
| `name` | `String` | Nombre/concepto (ej. "Café", "Regalo de mi tío"). |
| `category` | `String?` | Categoría simple opcional (ver nota de consolidación arriba). |
| `description` | `String?` | Descripción opcional. |
| `date` | `LocalDate` | Fecha del movimiento. |
| `recurringRuleId` | `Long?` (FK → `RecurringTransactionRule.id`) | Referencia si esta transacción fue generada por una regla recurrente. |
| `createdAt` | `Instant` | Timestamp real de registro en el sistema (puede diferir de `date` si se registra a posteriori). |

**Relaciones**: pertenece a un `Balance`; opcionalmente originada por un `RecurringTransactionRule`.

**Reglas de negocio aplicables**: BR-004, BR-005, BR-006, BR-007, BR-008.

**Inmutabilidad**: nunca se actualiza ni se borra tras su creación (BR-007); correcciones vía nuevas transacciones `ADJUSTMENT`.

---

### `RecurringTransactionRule`

Representa la configuración de un ingreso o gasto que se repite automáticamente.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK) | Identificador único. |
| `balanceId` | `Long` (FK → `Balance.id`) | Saldo destino/origen. |
| `type` | `TransactionType` enum (`INCOME, EXPENSE`) | Naturaleza de la regla. |
| `name` | `String` | Nombre (ej. "Nómina", "Spotify"). |
| `amount` | `BigDecimal` | Monto actual (puede cambiar en el tiempo — ver BR-010). |
| `periodicity` | `Periodicity` enum | Frecuencia de ejecución. |
| `dayOfExecution` | `Int` | Día del mes/semana en que se ejecuta, según periodicidad. |
| `startDate` | `LocalDate` | Fecha de inicio de la regla. |
| `endDate` | `LocalDate?` | Fecha de finalización opcional. |
| `isActive` | `Boolean` | Estado activo/inactivo. |
| `lastExecutedDate` | `LocalDate?` | Última fecha en que se generó una `Transaction` desde esta regla (para evitar duplicados — BR-009). |
| `description` | `String?` | Descripción opcional. |

**Relaciones**: pertenece a un `Balance`; genera múltiples `Transaction` en el tiempo.

**Reglas de negocio aplicables**: BR-009, BR-010.

---

## Entidades descartadas (justificación)

| Entidad propuesta originalmente | Decisión | Razón |
|---|---|---|
| `Income` | Fusionada en `Transaction` | Mismos campos y comportamiento que `Expense`, diferenciados solo por `type`. |
| `Expense` | Fusionada en `Transaction` | Ídem. |
| `RecurringIncome` | Fusionada en `RecurringTransactionRule` | Ídem, aplicado a reglas recurrentes. |
| `RecurringExpense` | Fusionada en `RecurringTransactionRule` | Ídem. |
| `Savings` | No es entidad | Es una clasificación de `Balance` (ADR-0001 punto 8). |
| `Category` | Campo simple, no entidad | Sin necesidad de jerarquía o gestión CRUD propia en el MVP. |
| `Budget` | No es entidad | `Balance` ya cumple ese rol. |

## Operaciones transaccionales (atómicas)

Estas operaciones deben ejecutarse dentro de una única transacción de base de datos (RNF-007):

1. **Registrar `Transaction`**: insertar el registro + actualizar `available` en el `Balance` destino y toda su cadena de ancestros + disparar reajuste (`rebalanceStrategy`) en hijos periódicos del `Balance` afectado, si aplica.
2. **Renovación de `Balance`**: aplicar `rolloverStrategy`, actualizar `available`, calcular siguiente `renewalDate`.
3. **Ejecución de `RecurringTransactionRule`**: generar la `Transaction` correspondiente + actualizar `lastExecutedDate` (para garantizar BR-009) + propagar efectos como en el punto 1.