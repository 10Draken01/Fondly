package lat.virgotp.fondly.ui_common.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import lat.virgotp.fondly.ui_common.atoms.BalanceAvatarIcon
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/** Encabezado del BalanceCard: icono, nombre, etiqueta de jerarquia y editar. */
@Composable
fun BalanceHeader(
    name: String,
    hierarchyLabel: String,
    icon: ImageVector,
    hierarchyColor: Color,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BalanceAvatarIcon(icon = icon, tint = hierarchyColor)
        Column(Modifier.padding(start = FondlySpacing.md).weight(1f)) {
            Text(
                name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(hierarchyLabel, style = MaterialTheme.typography.bodySmall, color = hierarchyColor)
        }
        if (onEditClick != null) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar saldo", tint = hierarchyColor)
            }
        }
    }
}