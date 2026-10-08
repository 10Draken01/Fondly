package lat.virgotp.fondly.infrastructure.persistence.room.mapper

import java.math.BigDecimal
import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import lat.virgotp.fondly.infrastructure.persistence.room.entity.BalanceEntity
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy
import lat.virgotp.fondly.domain.model.money

/**
 * Conversión monetaria EXCLUSIVA de infrastructure (ADR-0003):
 * BigDecimal (dominio) <-> Long centavos (SQLite). No existe TypeConverter
 * global de dinero a propósito.
 */

/** BigDecimal ($, escala 2) -> Long centavos. Falla si la escala excede 2 (nunca debería: dominio normaliza). */
internal fun BigDecimal.toMoneyCents(): Long = money().movePointRight(2).longValueExact()

/** Long centavos -> BigDecimal ($, escala 2). */
internal fun Long.toMoneyAmount(): BigDecimal = BigDecimal(this).movePointLeft(2)

fun BalanceEntity.toDomain(): Balance = Balance(
    id = id,
    name = name,
    targetAmount = targetAmount.toMoneyAmount(),
    available = available.toMoneyAmount(),
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
    targetAmount = targetAmount.toMoneyCents(),
    available = available.toMoneyCents(),
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
