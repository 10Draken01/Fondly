package lat.virgotp.fondly.infrastructure.persistence.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "balances",
    foreignKeys = [
        ForeignKey(
            entity = BalanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_balance_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("parent_balance_id")]
)
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    @ColumnInfo(name = "target_amount")
    val targetAmount: Double,
    val available: Double,
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