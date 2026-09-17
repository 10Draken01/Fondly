package lat.virgotp.fondly.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import lat.virgotp.fondly.application.usecase.GetAllBalancesUseCase

@HiltViewModel
class BalanceSectionsViewModel @Inject constructor(
    getAllBalances: GetAllBalancesUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val status = MutableStateFlow(BalanceSectionsStatusFilter.ACTIVE)
    private val parentFilter = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<BalanceSectionsUiState> = combine(
        getAllBalances(), query, status, parentFilter
    ) { all, q, s, p ->
        val parents = all.filter { it.parentBalanceId == null && it.isActive }
        val nameById = all.associate { it.id to it.name }
        val list = all
            .filter { it.parentBalanceId != null }
            .filter { p == null || it.parentBalanceId == p }
            .filter {
                when (s) {
                    BalanceSectionsStatusFilter.ACTIVE -> it.isActive
                    BalanceSectionsStatusFilter.INACTIVE -> !it.isActive
                    BalanceSectionsStatusFilter.ALL -> true
                }
            }
            .filter { q.isBlank() || it.name.contains(q, ignoreCase = true) }
        BalanceSectionsUiState(
            sections = list,
            parents = parents,
            nameById = nameById,
            parentFilter = p,
            searchQuery = q,
            statusFilter = s,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BalanceSectionsUiState())

    fun onSearchQueryChange(v: String) { query.value = v }
    fun onStatusFilterChange(v: BalanceSectionsStatusFilter) { status.value = v }
    fun onParentFilterChange(v: Long?) { parentFilter.value = v }
}