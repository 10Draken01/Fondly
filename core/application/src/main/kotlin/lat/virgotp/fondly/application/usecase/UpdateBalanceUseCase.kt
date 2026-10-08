package lat.virgotp.fondly.application.usecase

import java.math.BigDecimal
import lat.virgotp.fondly.domain.exception.BalanceCycleException
import lat.virgotp.fondly.domain.exception.ParentBalanceExceededException
import lat.virgotp.fondly.domain.exception.ParentBalanceNotFoundException
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.model.money
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class UpdateBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(balance: Balance): Result<Unit> {
        return try {
            val updatedBalance = balance.normalized()

            // Guardia de escritura anti-ciclos (BR-001): el nuevo padre no
            // puede ser el propio balance ni uno de sus descendientes.
            updatedBalance.parentBalanceId?.let { newParentId ->
                validateNoCycle(updatedBalance.id, newParentId)
            }

            updatedBalance.parentBalanceId?.let { parentId ->
                val parent = repository.getById(parentId)
                    ?: throw ParentBalanceNotFoundException(parentId)

                val siblings = repository.getActiveChildrenOf(parentId)
                    .filter { it.id != updatedBalance.id }
                val sumOfSiblings = siblings.fold(BigDecimal.ZERO) { acc, b -> acc + b.targetAmount }
                val attemptedTotal = sumOfSiblings + updatedBalance.targetAmount

                if (attemptedTotal > parent.available) {
                    throw ParentBalanceExceededException(
                        parentAvailable = parent.available,
                        attemptedTotal = attemptedTotal
                    )
                }
            }

            repository.update(updatedBalance)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun validateNoCycle(balanceId: Long, newParentId: Long) {
        if (newParentId == balanceId) throw BalanceCycleException(balanceId, newParentId)
        val descendants = repository.collectDescendants(balanceId)
        if (descendants.any { it.id == newParentId }) {
            throw BalanceCycleException(balanceId, newParentId)
        }
    }
}
