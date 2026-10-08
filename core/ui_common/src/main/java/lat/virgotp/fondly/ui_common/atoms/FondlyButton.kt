package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.theme.Motion
import lat.virgotp.fondly.ui_common.theme.GoldGradientEnd
import lat.virgotp.fondly.ui_common.theme.GoldGradientStart
import lat.virgotp.fondly.ui_common.theme.SuccessGreen

enum class FondlyButtonVariant { Primary, Secondary, Ghost, UtilRed, UtilTriggerOff, UtilTriggerOn, UtilSuccess }

/**
 * Boton premium dorado.
 * - Primary: degradado dorado con texto oscuro (accion principal).
 * - Secondary: contorno dorado con texto dorado.
 * - Ghost: solo texto dorado.
 * - UtilRed: Para botones de borrar o por el estilo
 * - UtilTriggerOff: Para botones que cambien de estado (apagar/desactivar)
 * - UtilTriggerOn: Para botones que cambien de estado (encender/activar)
 * - UtilSuccess: Para acciones positivas puntuales (ej. renovar)
 */
@Composable
fun FondlyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    variant: FondlyButtonVariant = FondlyButtonVariant.Primary,
    icon: ImageVector? = null
) {
    val scheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(Motion.DURATION_SHORT),
        label = "buttonScale"
    )

    val shape = RoundedCornerShape(16.dp)
    val contentColor = when (variant) {
        FondlyButtonVariant.Primary -> Color(0xFF2A2005)
        FondlyButtonVariant.Secondary -> scheme.primary
        FondlyButtonVariant.Ghost -> scheme.primary
        FondlyButtonVariant.UtilRed -> scheme.errorContainer
        FondlyButtonVariant.UtilTriggerOff -> scheme.onSurfaceVariant
        FondlyButtonVariant.UtilTriggerOn -> scheme.surfaceVariant
        FondlyButtonVariant.UtilSuccess -> SuccessGreen
    }

    val variantModifier = when (variant) {
        FondlyButtonVariant.Primary -> Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(GoldGradientStart, GoldGradientEnd)
            ),
            shape = shape
        )
        FondlyButtonVariant.Secondary -> Modifier.border(
            width = 1.dp,
            color = scheme.primary.copy(alpha = 0.55f),
            shape = shape
        )
        FondlyButtonVariant.Ghost -> Modifier
        FondlyButtonVariant.UtilRed -> Modifier.border(
            width = 1.dp,
            color = scheme.onErrorContainer.copy(alpha = 0.55f),
            shape = shape
        )
        FondlyButtonVariant.UtilTriggerOff -> Modifier.background(
            color = scheme.surfaceVariant,
            shape = shape
        )
        FondlyButtonVariant.UtilTriggerOn -> Modifier.background(
            color = scheme.onSurfaceVariant,
            shape = shape
        )
        FondlyButtonVariant.UtilSuccess -> Modifier.border(
            width = 1.dp,
            color = SuccessGreen.copy(alpha = 0.55f),
            shape = shape
        )
    }

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        interactionSource = interactionSource,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = if (variant == FondlyButtonVariant.Primary) {
                Color(0xFFD9D2C0).copy(alpha = 0.6f)
            } else {
                Color.Transparent
            },
            disabledContentColor = scheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(variantModifier)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}