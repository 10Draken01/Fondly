package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun FondlySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FondlyTextField(
        value = query,
        onValueChange = onQueryChange,
        label = "Buscar saldos",
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = modifier
    )
}