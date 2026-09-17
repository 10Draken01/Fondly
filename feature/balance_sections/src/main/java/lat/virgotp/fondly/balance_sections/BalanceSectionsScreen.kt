package lat.virgotp.fondly.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import lat.virgotp.fondly.balance_sections.components.atoms.ParentFilterDropdown
import lat.virgotp.fondly.ui_common.atoms.FondlySearchField
import lat.virgotp.fondly.ui_common.organisms.BalanceCard
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/** Sección [Sections]: solo saldos hijo, con filtro por padre (RF-034). */
@Composable
fun BalanceSectionsScreen(
    onBalanceClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: BalanceSectionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme

    FondlyScreenScaffold(title = "Sections") { paddingValues ->
        LazyColumn(
            Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = FondlySpacing.sm, bottom = 40.dp)
        ) {
            item(key = "filters") {
                Column(Modifier.padding(bottom = FondlySpacing.md)) {
                    ParentFilterDropdown(
                        parents = uiState.parents,
                        selectedParentId = uiState.parentFilter,
                        onSelected = viewModel::onParentFilterChange
                    )
                    Spacer(Modifier.height(FondlySpacing.sm))
                    FondlySearchField(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange
                    )
                    Spacer(Modifier.height(FondlySpacing.sm))
                    Row(horizontalArrangement = Arrangement.spacedBy(FondlySpacing.sm)) {
                        BalanceSectionsStatusFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = uiState.statusFilter == filter,
                                onClick = { viewModel.onStatusFilterChange(filter) },
                                label = {
                                    Text(when (filter) {
                                        BalanceSectionsStatusFilter.ACTIVE -> "Activos"
                                        BalanceSectionsStatusFilter.INACTIVE -> "Inactivos"
                                        BalanceSectionsStatusFilter.ALL -> "Todos"
                                    })
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = scheme.primary.copy(alpha = 0.18f),
                                    selectedLabelColor = scheme.primary
                                )
                            )
                        }
                    }
                }
            }

            when {
                uiState.isLoading -> item(key = "loading") {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = scheme.primary)
                    }
                }
                uiState.sections.isEmpty() -> item(key = "empty") {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay Sections para este filtro.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
                else -> itemsIndexed(uiState.sections, key = { _, b -> b.id }) { _, Section ->
                    BalanceCard(
                        balance = Section,
                        depth = 1,
                        parentName = uiState.nameById[Section.parentBalanceId],
                        onClick = { onBalanceClick(Section.id) },
                        onEditClick = { onEditClick(Section.id) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}