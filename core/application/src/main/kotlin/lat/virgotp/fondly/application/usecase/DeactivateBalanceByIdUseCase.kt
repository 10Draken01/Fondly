package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class DeactivateBalanceByIdUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            val children = repository.getActiveChildrenOf(id) // hijos DIRECTOS activos
            for (c in children) {
                repository.deactivate(c.id)
            }
            repository.deactivate(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}