package lat.virgotp.fondly.infrastructure.persistence.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla `transactions` — ledger inmutable (BR-007).
 *
 * - `amount` es el PRECIO UNITARIO en centavos MXN; el importe efectivo es
 *   `amount × quantity` (calculado, nunca persistido).
 * - `date` es la fecha contable elegida por el usuario; `created_at` es el
 *   timestamp real de auditoría.
 * - `recurring_rule_id` NULL = transacción manual.
 * - Solo INSERT y SELECT a nivel DAO: no existen UPDATE/DELETE para esta tabla.
 *
 * FKs con RESTRICT (sin CASCADE): una transacción nunca queda huérfana y un
 * Balance/Regla con historial no puede eliminarse físicamente (BR-003).
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = BalanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["balance_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = RecurringTransactionRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurring_rule_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        // RF-029: historial por saldo + rango de fechas con un solo índice.
        Index(value = ["balance_id", "date"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "balance_id")
    val balanceId: Long,
    val type: String, // INCOME, EXPENSE, ALLOCATION, ADJUSTMENT
    val amount: Long, // centavos MXN, precio UNITARIO, CHECK(amount > 0) vía SQL
    val quantity: Int, // unidades, CHECK(quantity > 0) vía SQL, default 1
    val name: String,
    val category: String?,
    val description: String?,
    val date: String, // ISO-8601, fecha contable
    @ColumnInfo(name = "recurring_rule_id")
    val recurringRuleId: Long?,
    @ColumnInfo(name = "created_at")
    val createdAt: String // ISO-8601, timestamp de auditoría
)
