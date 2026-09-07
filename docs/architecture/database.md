# Base de Datos Local

## Motor: Room (sobre SQLite)

Justificación: es la solución oficial de Android/Jetpack para persistencia local relacional, con soporte de tipo-seguridad en tiempo de compilación, migraciones estructuradas, y soporte nativo de `Flow`/`StateFlow` para observar cambios reactivamente en la UI — exactamente lo que necesita el dashboard de saldos. Se descarta una base de datos NoSQL local (ej. Realm) porque el dominio es inherentemente relacional (saldos con jerarquía, transacciones con claves foráneas) y Room ya cubre todas las necesidades sin dependencias adicionales.

## Disponible: ¿almacenado o calculado?

Ver [ADR-0001, punto 9](./decisions.md): se opta por **ambos**. `available` se almacena como columna en `balances` (para lecturas rápidas), pero siempre debe ser reconstruible sumando `transactions` — existe una función de recálculo (`RecalculateBalanceUseCase`) para verificar/corregir consistencia. Esto se explica con más detalle en `architecture.md`.

## Esquema

### Tabla `balances`

| Columna | Tipo SQLite | Constraints |
|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT |
| `name` | TEXT | NOT NULL |
| `target_amount` | REAL | NOT NULL |
| `available` | REAL | NOT NULL, DEFAULT 0 |
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
| `amount` | REAL | NOT NULL, CHECK(`amount` > 0) |
| `quantity` | INTEGER | NOT NULL, DEFAULT 1 |
| `name` | TEXT | NOT NULL |
| `category` | TEXT | NULL |
| `description` | TEXT | NULL |
| `date` | TEXT | NOT NULL (ISO-8601, solo fecha) |
| `recurring_rule_id` | INTEGER | NULL, FOREIGN KEY → `recurring_transaction_rules(id)` |
| `created_at` | TEXT | NOT NULL (ISO-8601, timestamp completo) |

**Índices**: `idx_transactions_balance_id` sobre `balance_id` (consultas de historial por saldo — RF-028); `idx_transactions_date` sobre `date` (consultas por rango de fechas).

**Constraint de integridad referencial**: `FOREIGN KEY (balance_id) REFERENCES balances(id) ON DELETE RESTRICT` — una transacción nunca debe quedar huérfana; refuerza BR-003 (no se puede eliminar un `Balance` con historial).

**Inmutabilidad (BR-007)**: no se define ninguna operación `UPDATE` sobre esta tabla a nivel de DAO — solo `INSERT` y `SELECT`. Las correcciones se insertan como nuevas filas con `type = 'ADJUSTMENT'`.

### Tabla `recurring_transaction_rules`

| Columna | Tipo SQLite | Constraints |
|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT |
| `balance_id` | INTEGER | NOT NULL, FOREIGN KEY → `balances(id)` |
| `type` | TEXT | NOT NULL (`INCOME`, `EXPENSE`) |
| `name` | TEXT | NOT NULL |
| `amount` | REAL | NOT NULL |
| `periodicity` | TEXT | NOT NULL |
| `day_of_execution` | INTEGER | NOT NULL |
| `start_date` | TEXT | NOT NULL (ISO-8601) |
| `end_date` | TEXT | NULL |
| `is_active` | INTEGER | NOT NULL, DEFAULT 1 (boolean) |
| `last_executed_date` | TEXT | NULL (ISO-8601) |
| `description` | TEXT | NULL |

**Índices**: `idx_recurring_rules_balance_id`; `idx_recurring_rules_is_active` (el worker de ejecución solo consulta reglas activas).

**Constraint de integridad referencial**: `FOREIGN KEY (balance_id) REFERENCES balances(id) ON DELETE RESTRICT`.

## Prevención de duplicados en recurrencia (BR-009)

La combinación de `last_executed_date` en `recurring_transaction_rules` + una verificación idempotente en el Use Case de ejecución (comparar contra la fecha esperada del periodo actual antes de insertar) evita duplicados incluso si el `Worker` de background se dispara más de una vez para el mismo periodo (ver detalle de implementación en la sección de automatizaciones Android, a definir en la Fase 4).

## Migraciones

Se usará el sistema de migraciones explícitas de Room (`Migration(from, to)`), nunca `fallbackToDestructiveMigration()` en producción — perder datos financieros del usuario en una actualización sería inaceptable dado el propósito de la app. Cada cambio de esquema en el futuro debe acompañarse de su migración correspondiente y una prueba que la valide.

## Diagrama de relaciones (simplificado)

```
balances (self-referencing: parent_balance_id)
   │ 1
   │
   │ N
transactions ──── N:1 ──── recurring_transaction_rules
```