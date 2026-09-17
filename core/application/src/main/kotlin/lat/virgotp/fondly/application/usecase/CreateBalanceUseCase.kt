package lat.virgotp.fondly.application.usecase

import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import lat.virgotp.fondly.domain.exception.ParentBalanceExceededException
import lat.virgotp.fondly.domain.exception.ParentBalanceNotFoundException
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class CreateBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(
        name: String,
        targetAmount: Double,
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
            if (parentBalanceId != null) {
                validateAgainstParent(parentBalanceId, targetAmount)
            }

            val balance = Balance(
                name = name,
                targetAmount = targetAmount,
                available = targetAmount,
                parentBalanceId = parentBalanceId,
                description = description,
                createdAt = Clock.System.now(),
                type = type,
                periodicity = periodicity,
                renewalDate = renewalDate,
                rolloverStrategy = rolloverStrategy,
                rebalanceStrategy = rebalanceStrategy,
                allowOverdraft= allowOverdraft,
                notificationThreshold = notificationThreshold

            )

            Result.success(repository.insert(balance))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun validateAgainstParent(parentBalanceId: Long, newTargetAmount: Double) {
        val parent = repository.getById(parentBalanceId)
            ?: throw ParentBalanceNotFoundException(parentBalanceId)

        val existingChildren = repository.getActiveChildrenOf(parentBalanceId)
        val sumOfChildren = existingChildren.sumOf { it.targetAmount }
        val attemptedTotal = sumOfChildren + newTargetAmount

        if (attemptedTotal > parent.available) {
            throw ParentBalanceExceededException(
                parentAvailable = parent.available,
                attemptedTotal = attemptedTotal
            )
        }
    }
}