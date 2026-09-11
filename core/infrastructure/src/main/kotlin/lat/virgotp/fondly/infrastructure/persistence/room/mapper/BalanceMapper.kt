package lat.virgotp.fondly.infrastructure.persistence.room.mapper

import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy

fun BalanceEntity.toDomain(): Balance = Balance(
    id = id,
    name = name,
    targetAmount = targetAmount,
    available = available,
    periodicity = periodicity?.let { Periodicity.valueOf(it) } ?: Periodicity.NONE,
    renewalDate = renewalDate?.let { LocalDate.parse(it) },
    parentBalanceId = parentBalanceId,
    type = BalanceType.valueOf(type),
    rolloverStrategy = RolloverStrategy.valueOf(rolloverStrategy),
    rebalanceStrategy = RebalanceStrategy.valueOf(rebalanceStrategy),
    allowOverdraft = allowOverdraft,
    isActive = isActive,
    description = description,
    notificationThreshold = notificationThreshold,
    createdAt = Instant.parse(createdAt)
)

fun Balance.toEntity(): BalanceEntity = BalanceEntity(
    id = id,
    name = name,
    targetAmount = targetAmount,
    available = available,
    periodicity = if (periodicity == Periodicity.NONE) null else periodicity.name,
    renewalDate = renewalDate?.toString(),
    parentBalanceId = parentBalanceId,
    type = type.name,
    rolloverStrategy = rolloverStrategy.name,
    rebalanceStrategy = rebalanceStrategy.name,
    allowOverdraft = allowOverdraft,
    isActive = isActive,
    description = description,
    notificationThreshold = notificationThreshold,
    createdAt = createdAt.toString()
)