package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class GetBalanceByIdUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(id: Long): Balance? = repository.getById(id)
}