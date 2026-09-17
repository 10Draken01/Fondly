package lat.virgotp.fondly.sections

import lat.virgotp.fondly.domain.model.Balance

enum class BalanceSectionsStatusFilter { ACTIVE, INACTIVE, ALL }

data class BalanceSectionsUiState(
    val sections: List<Balance> = emptyList(),
    val parents: List<Balance> = emptyList(),
    val nameById: Map<Long, String> = emptyMap(),
    val parentFilter: Long? = null,
    val searchQuery: String = "",
    val statusFilter: BalanceSectionsStatusFilter = BalanceSectionsStatusFilter.ACTIVE,
    val isLoading: Boolean = true
)