package lat.virgotp.fondly.infrastructure.persistence.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla `balances`.
 * Dinero: columnas monetarias son INTEGER en CENTAVOS (100 = $1.00 MXN).
 * La conversión BigDecimal <-> centavos vive exclusivamente en BalanceMapper
 * (ADR-0003). Jamás exponer Double/Float para dinero.
 */
@Entity(
    tableName = "balances",
    foreignKeys = [
        ForeignKey(
            entity = BalanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_balance_id"],
            onDelete = ForeignKey.RESTRICT // BR-003: prohibido CASCADE en todo el esquema
        )
    ],
    indices = [Index("parent_balance_id")]
)
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    @ColumnInfo(name = "target_amount")
    val targetAmount: Long, // centavos MXN
    val available: Long,    // centavos MXN (caché materializado, ver INV-1)
    val periodicity: String?,
    @ColumnInfo(name = "renewal_date")
    val renewalDate: String?,
    @ColumnInfo(name = "parent_balance_id")
    val parentBalanceId: Long?,
    val type: String,
    @ColumnInfo(name = "rollover_strategy")
    val rolloverStrategy: String,
    @ColumnInfo(name = "rebalance_strategy")
    val rebalanceStrategy: String,
    @ColumnInfo(name = "allow_overdraft")
    val allowOverdraft: Boolean,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    val description: String?,
    @ColumnInfo(name = "notification_threshold")
    val notificationThreshold: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)
