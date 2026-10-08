package lat.virgotp.fondly.balances.dashboard

import lat.virgotp.fondly.domain.model.Balance

enum class BalanceStatusFilter { ACTIVE, INACTIVE, ALL }

data class DashboardUiState(
    val balances: List<Balance> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: BalanceStatusFilter = BalanceStatusFilter.ACTIVE,
    val isLoading: Boolean = true,
    val childrenByParent: Map<Long, List<Balance>> = emptyMap()
)