package lat.virgotp.fondly.ui_common.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object Motion {
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 450

    /** Retardo entre items para animaciones escalonadas (stagger). */
    const val STAGGER_MS = 70L

    fun <T> defaultTween() =
        tween<T>(durationMillis = DURATION_MEDIUM, easing = FastOutSlowInEasing)

    fun <T> gentleSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
}