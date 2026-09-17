package lat.virgotp.fondly.feature.balances.detail

import lat.virgotp.fondly.domain.model.Balance

data class BalanceDetailUiState(
    val balance: Balance? = null,
    val children: List<Balance> = emptyList(),
    val isLoading: Boolean = true
)