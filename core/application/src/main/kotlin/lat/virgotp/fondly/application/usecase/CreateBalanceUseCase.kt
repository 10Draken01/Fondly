package lat.virgotp.fondly.application.usecase

import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import kotlin.time.Clock
import lat.virgotp.fondly.domain.exception.ParentBalanceExceededException
import lat.virgotp.fondly.domain.exception.ParentBalanceNotFoundException
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy
import lat.virgotp.fondly.domain.model.money
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class CreateBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(
        name: String,
        targetAmount: BigDecimal,
        parentBalanceId: Long? = null,
        description: String? = null,
        type: BalanceType,
        periodicity: Periodicity,
        renewalDate: LocalDate?,
        rolloverStrategy: RolloverStrategy,
        rebalanceStrategy: RebalanceStrategy,
        allowOverdraft: Boolean,
        notificationThreshold: Int?
    ): Result<Long> {
        return try {
            val normalizedTarget = targetAmount.money()
            if (parentBalanceId != null) {
                validateAgainstParent(parentBalanceId, normalizedTarget)
            }

            val balance = Balance(
                name = name,
                targetAmount = normalizedTarget,
                available = normalizedTarget,
                parentBalanceId = parentBalanceId,
                description = description,
                createdAt = Clock.System.now(),
                type = type,
                periodicity = periodicity,
                renewalDate = renewalDate,
                rolloverStrategy = rolloverStrategy,
                rebalanceStrategy = rebalanceStrategy,
                allowOverdraft = allowOverdraft,
                notificationThreshold = notificationThreshold
            )

            Result.success(repository.insert(balance))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun validateAgainstParent(parentBalanceId: Long, newTargetAmount: BigDecimal) {
        val parent = repository.getById(parentBalanceId)
            ?: throw ParentBalanceNotFoundException(parentBalanceId)

        val existingChildren = repository.getActiveChildrenOf(parentBalanceId)
        // BigDecimal: sumas con fold/reduce y comparaciones con operadores
        // basados en compareTo, nunca equals() (ADR-0003).
        val sumOfChildren = existingChildren.fold(BigDecimal.ZERO) { acc, b -> acc + b.targetAmount }
        val attemptedTotal = sumOfChildren + newTargetAmount

        if (attemptedTotal > parent.available) {
            throw ParentBalanceExceededException(
                parentAvailable = parent.available,
                attemptedTotal = attemptedTotal
            )
        }
    }
}
