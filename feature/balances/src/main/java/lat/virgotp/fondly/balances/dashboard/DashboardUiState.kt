package lat.virgotp.fondly.feature.balances.dashboard

import lat.virgotp.fondly.domain.model.Balance

data class DashboardUiState(
    val balances: List<Balance> = emptyList(),
    val isLoading: Boolean = true
)