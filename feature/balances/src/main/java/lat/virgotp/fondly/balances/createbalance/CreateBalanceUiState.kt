package lat.virgotp.fondly.balances.createbalance

import kotlinx.datetime.LocalDate
import lat.virgotp.fondly.domain.model.*

data class CreateBalanceUiState(
    val name: String = "",
    val targetAmount: String = "",
    val description: String = "",
    val type: BalanceType = BalanceType.REGULAR,
    val periodicity: Periodicity = Periodicity.NONE,
    val renewalDate: LocalDate? = null,
    val rolloverStrategy: RolloverStrategy = RolloverStrategy.RESET,
    val rebalanceStrategy: RebalanceStrategy = RebalanceStrategy.EVEN,
    val allowOverdraft: Boolean = false,
    val notificationThreshold: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
)