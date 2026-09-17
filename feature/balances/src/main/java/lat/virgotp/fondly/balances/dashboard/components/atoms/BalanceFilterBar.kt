package lat.virgotp.fondly.balances.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lat.virgotp.fondly.balances.dashboard.BalanceStatusFilter
import lat.virgotp.fondly.ui_common.atoms.FondlySearchField
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/** Buscador + chips de estado: Activos (por defecto) / Inactivos / Todos (RF-033). */
@Composable
fun BalanceFilterBar(
    query: String,
    statusFilter: BalanceStatusFilter,
    onQueryChange: (String) -> Unit,
    onStatusFilterChange: (BalanceStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth().padding(bottom = FondlySpacing.md)) {
        FondlySearchField(query = query, onQueryChange = onQueryChange)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = FondlySpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(FondlySpacing.sm)
        ) {
            BalanceStatusFilter.entries.forEach { filter ->
                FilterChip(
                    selected = statusFilter == filter,
                    onClick = { onStatusFilterChange(filter) },
                    label = {
                        Text(
                            when (filter) {
                                BalanceStatusFilter.ACTIVE -> "Activos"
                                BalanceStatusFilter.INACTIVE -> "Inactivos"
                                BalanceStatusFilter.ALL -> "Todos"
                            }
                        )
                    },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = scheme.primary.copy(alpha = 0.18f),
                        selectedLabelColor = scheme.primary
                    )
                )
            }
        }
    }
}