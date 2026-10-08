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
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.atoms.BarSegment
import lat.virgotp.fondly.ui_common.atoms.DistributedSegmentedBar
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.uicommon.R
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.util.FondlyMoney
import kotlin.math.roundToInt

/**
 * Fila de estadistica simple con 2 segmentos: [valor][vacio], separados
 * (estilo card), con su propia leyenda de colores debajo. Pensada para
 * metricas de resumen a nivel raiz (Total, Libre), no para balances con
 * descendientes propios (para eso ver como el detalle arma las filas de
 * apartados usando buildBalanceCardSegments + BalanceProgressSection).
 */
@Composable
fun SegmentedStatRow(
    label: String,
    amount: BigDecimal,
    fraction: Float,
    color: Color,
    emptyColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val scheme = MaterialTheme.colorScheme
    val currency = FondlyCurrencyController.rememberCurrency()
    val clamped = fraction.coerceIn(0f, 1f)
    val segments = listOf(
        BarSegment(clamped, color, label),
        BarSegment(1f - clamped, emptyColor, stringResource(R.string.segment_empty))
    ).filter { it.fraction > 0f }
    val legendItems = segments.map { LegendItem(it.label, it.color) }

    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                } else {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                }
                Spacer(Modifier.width(FondlySpacing.xs))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
            }
            Text(
                "${FondlyMoney.format(amount, currency)} · ${(clamped * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(FondlySpacing.xs))
        DistributedSegmentedBar(segments = segments)
        Spacer(Modifier.height(FondlySpacing.xs))
        SegmentLegend(items = legendItems)
    }
}