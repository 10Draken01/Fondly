package lat.virgotp.fondly.balance_sections.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.feature.sections.R
import lat.virgotp.fondly.ui_common.molecules.FondlyDropdownField

/** Filtro por saldo padre: "Todos los saldos" + cada saldo base (RF-034). */
@Composable
fun ParentFilterDropdown(
    parents: List<Balance>,
    selectedParentId: Long?,
    onSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allLabel = stringResource(R.string.sections_all_parents)
    val options = listOf(allLabel) + parents.map { it.name }
    val selectedIndex = selectedParentId
        ?.let { id -> parents.indexOfFirst { it.id == id } }
        ?.takeIf { it >= 0 }
        ?.plus(1) ?: 0

    FondlyDropdownField(
        label = stringResource(R.string.sections_parent_filter),
        options = options,
        selectedIndex = selectedIndex,
        onSelected = { index -> onSelected(if (index == 0) null else parents[index - 1].id) },
        modifier = modifier
    )
}