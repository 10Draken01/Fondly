package lat.virgotp.fondly.ui_common.molecules

import java.math.BigDecimal
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.atoms.FondlyProgressBar
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.util.FondlyMoney
import kotlin.math.roundToInt

/**
 * Fila individual de estadistica: icono/punto de color + nombre a la
 * izquierda, monto + porcentaje a la derecha, y debajo una barra de
 * progreso propia (10dp por defecto). Soporta indentacion para reflejar
 * niveles de jerarquia (hijo, nieto, bisnieto...).
 */
@Composable
fun LabeledProgressStat(
    label: String,
    amount: BigDecimal,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    indentLevel: Int = 0,
    barHeight: androidx.compose.ui.unit.Dp = 10.dp
) {
    val scheme = MaterialTheme.colorScheme
    val currency = FondlyCurrencyController.rememberCurrency()
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = (indentLevel * 16).dp, top = FondlySpacing.xs, bottom = FondlySpacing.xs)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                } else {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                }
                Spacer(Modifier.width(FondlySpacing.xs))
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                "${FondlyMoney.format(amount, currency)} · ${(fraction * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(FondlySpacing.xs))
        FondlyProgressBar(fraction = fraction, progressColor = color, barHeight = barHeight)
    }
}