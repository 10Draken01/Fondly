package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.exception.ParentBalanceExceededException
import lat.virgotp.fondly.domain.exception.ParentBalanceNotFoundException
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class UpdateBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(balance: Balance): Result<Unit> {
        return try {
            balance.parentBalanceId?.let { parentId ->
                val parent = repository.getById(parentId)
                    ?: throw ParentBalanceNotFoundException(parentId)

                val siblings = repository.getActiveChildrenOf(parentId)
                    .filter { it.id != balance.id }
                val sumOfSiblings = siblings.sumOf { it.targetAmount }
                val attemptedTotal = sumOfSiblings + balance.targetAmount

                if (attemptedTotal > parent.available) {
                    throw ParentBalanceExceededException(
                        parentAvailable = parent.available,
                        attemptedTotal = attemptedTotal
                    )
                }
            }

            repository.update(balance)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}