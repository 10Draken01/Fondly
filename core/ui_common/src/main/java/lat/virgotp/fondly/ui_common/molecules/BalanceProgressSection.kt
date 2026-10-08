package lat.virgotp.fondly.ui_common.molecules

import java.math.BigDecimal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lat.virgotp.fondly.ui_common.atoms.AmountOfTotalText
import lat.virgotp.fondly.ui_common.atoms.BarSegment
import lat.virgotp.fondly.ui_common.atoms.DistributedSegmentedBar
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.uicommon.R
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import kotlin.math.roundToInt

/**
 * Seccion de progreso del BalanceCard: barra segmentada individual
 * (con ancho minimo garantizado), leyenda scrolleable y resumen
 * textual "$libre de $total" + % libre.
 */
@Composable
fun BalanceProgressSection(
    segments: List<BarSegment>,
    legendItems: List<LegendItem>,
    available: BigDecimal,
    target: BigDecimal,
    freeFraction: Float,
    freeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DistributedSegmentedBar(segments = segments)

        if (legendItems.isNotEmpty()) {
            SegmentLegend(items = legendItems, modifier = Modifier.padding(top = FondlySpacing.xs))
        }

        Row(
            Modifier.fillMaxWidth().padding(top = FondlySpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            AmountOfTotalText(current = available, total = target)
            Text(
                stringResource(R.string.balance_percent_free, (freeFraction * 100).roundToInt()),
                style = MaterialTheme.typography.labelLarge,
                color = freeColor
            )
        }
    }
}