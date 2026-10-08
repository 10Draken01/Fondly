package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.util.distributeSegmentWidths

/** Un segmento individual de la barra: fraccion (0f-1f), color y etiqueta para la leyenda. */
data class BarSegment(val fraction: Float, val color: Color, val label: String)

/**
 * Barra de progreso con segmentos INDIVIDUALES separados por [gap].
 * Cada segmento respeta un ancho minimo ([minSegmentWidth]) para que
 * porcentajes muy pequenos sigan siendo visibles; el espacio se
 * redistribuye entre los demas segmentos (ver [distributeSegmentWidths]).
 */
@Composable
fun DistributedSegmentedBar(
    segments: List<BarSegment>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 10.dp,
    gap: Dp = 3.dp,
    minSegmentWidth: Dp = 4.dp,
    cornerRadius: Dp = 3.dp
) {
    val visible = segments.filter { it.fraction > 0f }
    if (visible.isEmpty()) return

    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(barHeight)) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val gapPx = with(density) { gap.toPx() }
        val minWidthPx = with(density) { minSegmentWidth.toPx() }
        val n = visible.size
        val usableWidthPx = (totalWidthPx - gapPx * (n - 1).coerceAtLeast(0)).coerceAtLeast(0f)

        val widthsPx = distributeSegmentWidths(
            fractions = visible.map { it.fraction },
            totalWidthPx = usableWidthPx,
            minWidthPx = minWidthPx
        )

        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            visible.forEachIndexed { index, segment ->
                val widthDp = with(density) { widthsPx[index].toDp() }
                Box(
                    modifier = Modifier
                        .width(widthDp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(segment.color)
                )
            }
        }
    }
}