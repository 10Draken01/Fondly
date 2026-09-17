package lat.virgotp.fondly.ui_common.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.atoms.GlassSurface
import lat.virgotp.fondly.ui_common.atoms.InfoRow
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

data class InfoRowData(val label: String, val value: String, val icon: ImageVector? = null)

/**
 * "Tabla" estilizada: card con titulo (+icono) y filas label/valor
 * separadas por divisores sutiles. Usalo para cualquier grupo de
 * datos tabulares (configuracion, fechas, metadatos, etc.).
 */
@Composable
fun InfoSectionCard(
    title: String,
    rows: List<InfoRowData>,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    titleIcon: ImageVector? = null
) {
    if (rows.isEmpty()) return
    GlassSurface(modifier = modifier, glowColor = accentColor) {
        Column(Modifier.padding(FondlySpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (titleIcon != null) {
                    Icon(titleIcon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(FondlySpacing.sm))
                }
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(FondlySpacing.xs))
            rows.forEachIndexed { index, row ->
                InfoRow(label = row.label, value = row.value, icon = row.icon)
                if (index != rows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }
        }
    }
}