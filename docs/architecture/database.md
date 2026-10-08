# Base de Datos Local

## Motor: Room (sobre SQLite)

Justificación: es la solución oficial de Android/Jetpack para persistencia local relacional, con soporte de tipo-seguridad en tiempo de compilación, migraciones estructuradas, y soporte nativo de `Flow`/`StateFlow` para observar cambios reactivamente en la UI — exactamente lo que necesita el dashboard de saldos. Se descarta una base de datos NoSQL local (ej. Realm) porque el dominio es inherentemente relacional (saldos con jerarquía, transacciones con claves foráneas) y Room ya cubre todas las necesidades sin dependencias adicionales.

## Disponible: ¿almacenado o calculado?

Ver [ADR-0001, punto 9](./decisions.md) y [ADR-0003](./decisions.md): se opta por **ambos**. `available` se almacena como columna en `balances` (para lecturas rápidas), pero siempre debe ser reconstruible sumando `transactions` — existe una función de recálculo (`RecalculateBalanceUseCase`) para verificar/corregir consistencia. Esto se explica con más detalle en `architecture.md`.

## Esquema

### Tabla `balances`

| Columna | Tipo SQLite | Constraints |
|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT |
| `name` | TEXT | NOT NULL |
| `target_amount` | INTEGER (centavos MXN) | NOT NULL |
| `available` | INTEGER (centavos MXN) | NOT NULL |
| `periodicity` | TEXT | NULL (enum serializado) |
| `renewal_date` | TEXT | NULL (ISO-8601) |
| `parent_balance_id` | INTEGER | NULL, FOREIGN KEY → `balances(id)` |
| `type` | TEXT | NOT NULL, DEFAULT 'REGULAR' |
| `rollover_strategy` | TEXT | NOT NULL, DEFAULT 'RESET' |
| `rebalance_strategy` | TEXT | NOT NULL, DEFAULT 'EVEN' |
| `allow_overdraft` | INTEGER | NOT NULL, DEFAULT 0 (boolean) |
| `is_active` | INTEGER | NOT NULL, DEFAULT 1 (boolean) |
| `description` | TEXT | NULL |
| `notification_threshold` | INTEGER | NULL |
| `created_at` | TEXT | NOT NULL (ISO-8601) |

**Índices**: `idx_balances_parent_balance_id` sobre `parent_balance_id` (acelera el recorrido de la cadena de ancestros/descendientes).

**Constraint de integridad referencial**: `FOREIGN KEY (parent_balance_id) REFERENCES balances(id) ON DELETE RESTRICT` — evita borrar un saldo padre mientras tenga hijos (se debe desvincular o desactivar primero).

### Tabla `transactions`

| Columna | Tipo SQLite | Constraints |
|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT |
| `balance_id` | INTEGER | NOT NULL, FOREIGN KEY → `balances(id)` |
| `type` | TEXT | NOT NULL (`INCOME`, `EXPENSE`, `ALLOCATION`, `ADJUSTMENT`) |
| `amount` | INTEGER (centavos MXN) | NOT NULL (`amount` > 0, validado en dominio — precio **unitario**) |
| `quantity` | INTEGER | NOT NULL, DEFAULT 1 |
| `name` | TEXT | NOT NULL |
| `category` | TEXT | NULL |
| `description` | TEXT | NULL |
| `date` | TEXT | NOT NULL (ISO-8601, solo fecha) |
| `recurring_rule_id` | INTEGER | NULL, FOREIGN KEY → `recurring_transaction_rules(id)` |
| `created_at` | TEXT | NOT NULL (ISO-8601, timestamp completo) |

**Índices**: `index_transactions_balance_id_date` compuesto sobre `(balance_id, date)` — cubre con una sola estructura el historial por saldo (prefijo) y por saldo+rango de fechas (RF-029). NO hay índice sobre `recurring_rule_id` (las reglas se desactivan, no se borran). Un índice global sobre `date` se evaluará con el resumen mensual (Sprint 7).

**Constraint de integridad referencial**: `FOREIGN KEY (balance_id) REFERENCES balances(id) ON DELETE RESTRICT` — una transacción nunca debe quedar huérfana; refuerza BR-003 (no se puede eliminar un `Balance` con historial).

**Inmutabilidad (BR-007)**: no se define ninguna operación `UPDATE` sobre esta tabla a nivel de DAO — solo `INSERT` y `SELECT`. Las correcciones se insertan como nuevas filas con `type = 'ADJUSTMENT'`.

### Tabla `recurring_transaction_rules`

| Columna | Tipo SQLite | Constraints |
|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT |
| `balance_id` | INTEGER | NOT NULL, FOREIGN KEY → `balances(id)` |
| `type` | TEXT | NOT NULL (`INCOME`, `EXPENSE`) |
| `name` | TEXT | NOT NULL |
| `amount` | INTEGER (centavos MXN) | NOT NULL (`amount` > 0, validado en dominio) |
| `periodicity` | TEXT | NOT NULL |
| `day_of_execution` | INTEGER | NOT NULL |
| `start_date` | TEXT | NOT NULL (ISO-8601) |
| `end_date` | TEXT | NULL |
| `is_active` | INTEGER | NOT NULL, DEFAULT 1 (boolean) |
| `last_executed_date` | TEXT | NULL (ISO-8601) |
| `description` | TEXT | NULL |

**Índices**: `index_recurring_transaction_rules_balance_id`; `index_recurring_transaction_rules_is_active` (el worker de ejecución solo consulta reglas activas).

**Constraint de integridad referencial**: `FOREIGN KEY (balance_id) REFERENCES balances(id) ON DELETE RESTRICT`.

## Prevención de duplicados en recurrencia (BR-009)

La combinación de `last_executed_date` en `recurring_transaction_rules` + una verificación idempotente en el Use Case de ejecución (comparar contra la fecha esperada del periodo actual antes de insertar) evita duplicados incluso si el `Worker` de background se dispara más de una vez para el mismo periodo (ver detalle de implementación en la sección de automatizaciones Android, a definir en la Fase 4).

## Migraciones

Se usa el sistema de migraciones explícitas de Room (`Migration(from, to)`); `fallbackToDestructiveMigration()` está **prohibido en cualquier build** — perder datos financieros del usuario en una actualización sería inaceptable dado el propósito de la app. `exportSchema = true` y los JSON de esquema se versionan en Git; cada migración se valida con `MigrationTestHelper` (ver ADR-0003).

## Dinero: representación física (ADR-0003)

Todas las columnas monetarias son `INTEGER` en **centavos MXN** (`12345` = `$123.45`). El dominio usa `BigDecimal` (escala 2, `HALF_EVEN`); la conversión ocurre únicamente en los mappers de infrastructure. Moneda única del MVP: **MXN** (sin tabla `Currency` ni campo por saldo).

## Renovaciones como ADJUSTMENT de sistema (ADR-0003)

Toda renovación que modifique materialmente `available` inserta una `Transaction` de tipo `ADJUSTMENT` generada por el sistema (distinguible por `type` + convención de `name`/`description` — no existe ninguna entidad nueva para esto). Así, `Balance.available` se reconstruye con agregación pura del ledger (invariante INV-1).

## Estructura de Transaction (ADR-0003)

`amount` es el precio **unitario** (`> 0`) y `quantity` las unidades (`> 0`); el importe efectivo es `amount × quantity` — calculado, nunca persistido. `date` es la fecha contable elegida por el usuario (ordena el historial); `created_at` es auditoría. `recurring_rule_id` NULL = transacción manual.

## Diagrama de relaciones (simplificado)

```
balances (self-referencing: parent_balance_id)
   │ 1
   │
   │ N
transactions ──── N:1 ──── recurring_transaction_rules
```