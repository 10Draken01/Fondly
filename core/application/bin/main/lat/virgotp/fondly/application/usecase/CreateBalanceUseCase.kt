package lat.virgotp.fondly.application.usecase

import kotlin.time.Clock
import lat.virgotp.fondly.domain.exception.ParentBalanceExceededException
import lat.virgotp.fondly.domain.exception.ParentBalanceNotFoundException
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class CreateBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(
        name: String,
        targetAmount: Double,
        parentBalanceId: Long? = null,
        description: String? = null
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
                createdAt = Clock.System.now()
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