package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.model.money
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

            // Recorrido con guardia defensiva anti-ciclos (ver BalanceHierarchyGuard).
            repository.update(parentBalance.copy(available = parentBalance.targetAmount.money()))
            repository.collectDescendants(parentId).forEach { child ->
                repository.update(child.copy(available = child.targetAmount.money()))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
