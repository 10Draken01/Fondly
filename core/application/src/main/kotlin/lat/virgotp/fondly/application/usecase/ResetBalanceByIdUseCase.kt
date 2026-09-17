package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class ResetBalanceByIdUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(parentId: Long): Result<Unit> {
        return try {
            val parentBalance = repository.getById(parentId) ?: return Result.failure(
                IllegalArgumentException("Balance $parentId no encontrado")
            )

            suspend fun recurse(balance: Balance) {
                val resetBalance = balance.copy(
                    available = balance.targetAmount
                )

                repository.update(resetBalance)

                val children = repository.getChildrenOf(balance.id)

                children.forEach { child ->
                    recurse(child)
                }
            }

            recurse(parentBalance)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}