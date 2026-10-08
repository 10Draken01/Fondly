package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Superficie de "vidrio mate" premium.
 *
 * Capas (de fondo a frente):
 *  1. Sombra suave con halo dorado.
 *  2. Relleno translucido vertical (de 0.82 a 0.60 del color surface).
 *  3. Glow radial dorado en la esquina superior.
 *  4. Brillo diagonal sutil (sheen).
 *  5. Grano mate (ruido determinista, ~140 puntos).
 *  6. Linea de luz interior superior.
 *  7. Borde con degradado de luz.
 *
 * Todo en Compose puro: funciona desde minSdk 24 (sin RenderEffect/blur).
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.large
    val scheme = MaterialTheme.colorScheme

    // Grano mate: determinista (semilla fija) para no parpadear entre recomposiciones.
    val noise = remember {
        val random = Random(seed = 42)
        List(140) { Offset(random.nextFloat(), random.nextFloat()) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.20f),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        scheme.surface.copy(alpha = 0.82f),
                        scheme.surface.copy(alpha = 0.60f)
                    )
                )
            )
            .drawBehind {
                val radius = CornerRadius(cornerRadius.toPx())

                // 3) Glow dorado radial, esquina superior
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width * 0.10f, 0f),
                        radius = size.width * 0.75f
                    )
                )

                // 4) Brillo diagonal
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height * 0.65f)
                    )
                )

                // 5) Grano mate
                val points = noise.map { Offset(it.x * size.width, it.y * size.height) }
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = Color.White.copy(alpha = 0.045f),
                    strokeWidth = 1.6f
                )

                // 6) Linea de luz interior superior
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
                    ),
                    topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                    size = size.copy(
                        width = size.width - 2.dp.toPx(),
                        height = size.height * 0.5f
                    ),
                    cornerRadius = radius,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            )
    ) {
        CompositionLocalProvider(LocalContentColor provides scheme.onSurface) {
            content()
        }
    }
}