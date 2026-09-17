package lat.virgotp.fondly.application.usecase

import kotlinx.coroutines.flow.Flow
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject

class GetActiveBalancesUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    operator fun invoke(): Flow<List<Balance>> = repository.getActiveBalances()
}