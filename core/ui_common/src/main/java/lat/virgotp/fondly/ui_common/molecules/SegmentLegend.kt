package lat.virgotp.fondly.ui_common.molecules

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.atoms.ColorDot

data class LegendItem(val label: String, val color: Color)

/**
 * Leyenda horizontal de la barra. Usa scroll horizontal nativo de
 * Compose: si el contenido cabe, no pasa nada (no hay barra de scroll
 * visible por defecto); si desborda, se puede deslizar.
 */
@Composable
fun SegmentLegend(
    items: List<LegendItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                ColorDot(color = item.color)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}