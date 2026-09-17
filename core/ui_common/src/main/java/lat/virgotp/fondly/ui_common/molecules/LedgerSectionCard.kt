package lat.virgotp.fondly.ui_common.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.atoms.LedgerRow
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

data class LedgerRowData(val label: String, val value: String, val icon: ImageVector? = null)

/**
 * Contenedor "libro de cuentas": card plana, borde fino y barra de
 * acento vertical. Desplegable: el titulo siempre es visible, tocar
 * el encabezado expande/colapsa las filas. Colapsada por defecto.
 */
@Composable
fun LedgerSectionCard(
    title: String,
    rows: List<LedgerRowData>,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    initiallyExpanded: Boolean = false
) {
    if (rows.isEmpty()) return
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevronRotation")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .border(width = 1.dp, color = scheme.outlineVariant, shape = shape)
            .background(scheme.surface)
    ) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(accentColor))
        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(FondlySpacing.lg),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = scheme.onSurface)
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = scheme.onSurfaceVariant,
                    modifier = Modifier.graphicsLayer { rotationZ = chevronRotation }
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(Modifier.padding(start = FondlySpacing.lg, end = FondlySpacing.lg, bottom = FondlySpacing.lg)) {
                    rows.forEachIndexed { index, row ->
                        LedgerRow(
                            label = row.label,
                            value = row.value,
                            icon = row.icon,
                            accentColor = accentColor,
                            zebra = index % 2 == 1
                        )
                        if (index != rows.lastIndex) {
                            HorizontalDivider(color = scheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}