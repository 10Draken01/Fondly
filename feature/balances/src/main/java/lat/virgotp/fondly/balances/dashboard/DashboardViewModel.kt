package lat.virgotp.fondly.balances.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import lat.virgotp.fondly.application.usecase.GetAllBalancesUseCase

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getAllBalances: GetAllBalancesUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val statusFilter = MutableStateFlow(BalanceStatusFilter.ACTIVE)

    val uiState: StateFlow<DashboardUiState> =
        combine(getAllBalances(), searchQuery, statusFilter) { all, query, filter ->
            val base = all.filter { it.parentBalanceId == null }
            val children = all.filter { it.parentBalanceId != null }
            val filtered = base.filter { b ->
                val statusOk = when (filter) {
                    BalanceStatusFilter.ACTIVE -> b.isActive
                    BalanceStatusFilter.INACTIVE -> !b.isActive
                    BalanceStatusFilter.ALL -> true
                }
                val queryOk = query.isBlank() || b.name.contains(query, ignoreCase = true)
                statusOk && queryOk
            }
            DashboardUiState(
                balances = filtered,
                searchQuery = query,
                statusFilter = filter,
                isLoading = false,
                childrenByParent = children.groupBy { it.parentBalanceId!! }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    fun onSearchQueryChange(query: String) { searchQuery.value = query }
    fun onStatusFilterChange(filter: BalanceStatusFilter) { statusFilter.value = filter }
}