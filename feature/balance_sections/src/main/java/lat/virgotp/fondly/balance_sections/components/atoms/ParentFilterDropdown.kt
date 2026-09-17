package lat.virgotp.fondly.balance_sections.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.ui_common.molecules.FondlyDropdownField

/** Filtro por saldo padre: "Todos los saldos" + cada saldo base (RF-034). */
@Composable
fun ParentFilterDropdown(
    parents: List<Balance>,
    selectedParentId: Long?,
    onSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Todos los saldos") + parents.map { it.name }
    val selectedIndex = selectedParentId
        ?.let { id -> parents.indexOfFirst { it.id == id } }
        ?.takeIf { it >= 0 }
        ?.plus(1) ?: 0

    FondlyDropdownField(
        label = "Saldo padre",
        options = options,
        selectedIndex = selectedIndex,
        onSelected = { index -> onSelected(if (index == 0) null else parents[index - 1].id) },
        modifier = modifier
    )
}