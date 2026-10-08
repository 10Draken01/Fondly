package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Barra segmentada: un tramo por cada apartado (grises) + resto = libre (pista visible). */
@Composable
fun FondlySegmentedProgressBar(
    segments: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
) {
    val total = segments.sumOf { it.first.toDouble() }.toFloat().coerceIn(0f, 1f)
    Row(
        modifier = modifier.fillMaxWidth().height(6.dp)
            .clip(RoundedCornerShape(50)).background(trackColor)
    ) {
        if (segments.isEmpty()) {
            Box(Modifier.fillMaxWidth().fillMaxHeight())
        } else {
            segments.forEach { (fraction, color) ->
                Box(
                    Modifier.fillMaxHeight()
                        .weight(fraction.coerceAtLeast(0.0001f))
                        .background(color)
                )
            }
            Box(Modifier.fillMaxHeight().weight((1f - total).coerceAtLeast(0.0001f)))
        }
    }
}