package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class DeactivateBalanceByIdUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            val childrens = repository.getActiveChildrenOf(id) // esto incluye al padre
            for( c in childrens ){
                repository.deactivate(c.id)
            }
            repository.deactivate(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}