package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Liston de esquina que indica el nivel de jerarquia (L1, L2, ...).
 * La tira va rotada 45 grados; el texto se contra-rota para leerse horizontal.
 */
@Composable
fun CornerRibbon(
    level: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                rotationZ = 45f
                translationX = size.width * 0.34f
                translationY = -size.height * 0.34f
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(color, color.copy(alpha = 0.82f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "L$level",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .offset( y = (12).dp)
                .graphicsLayer {
                rotationZ = -45f
            }
        )
    }
}