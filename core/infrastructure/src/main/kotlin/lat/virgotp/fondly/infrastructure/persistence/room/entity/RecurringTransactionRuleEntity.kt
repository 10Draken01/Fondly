package lat.virgotp.fondly.infrastructure.persistence.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla `recurring_transaction_rules` (D3). Creada en Room v2; su ejecución
 * automática llega en Sprint 3 (WorkManager). `amount` se persiste en
 * centavos MXN. `last_executed_date` es el mecanismo de idempotencia (BR-009)
 * y se mantiene desnormalizado deliberadamente.
 */
@Entity(
    tableName = "recurring_transaction_rules",
    foreignKeys = [
        ForeignKey(
            entity = BalanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["balance_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["balance_id"]),
        Index(value = ["is_active"]) // el worker solo consulta reglas activas
    ]
)
data class RecurringTransactionRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "balance_id")
    val balanceId: Long,
    val type: String, // INCOME, EXPENSE
    val name: String,
    val amount: Long, // centavos MXN, CHECK(amount > 0) vía SQL
    val periodicity: String,
    @ColumnInfo(name = "day_of_execution")
    val dayOfExecution: Int,
    @ColumnInfo(name = "start_date")
    val startDate: String, // ISO-8601
    @ColumnInfo(name = "end_date")
    val endDate: String?, // ISO-8601
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    @ColumnInfo(name = "last_executed_date")
    val lastExecutedDate: String?, // ISO-8601
    val description: String?
)
