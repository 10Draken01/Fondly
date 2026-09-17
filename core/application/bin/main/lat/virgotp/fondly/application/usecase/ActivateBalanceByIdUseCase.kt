package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class ActivateBalanceByIdUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            val childrens = repository.getDeactiveChildrenOf(id) // esto incluye al padre
            // si no tiene hijos no pasa nada
            for( c in childrens ){
                repository.activate(c.id)
            }
            repository.activate(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}