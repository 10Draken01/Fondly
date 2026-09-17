package lat.virgotp.fondly.ui_common.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Color por NIVEL de segmento dentro de la barra (1 = hijos directos,
 * 2 = nietos, 3+ = niveles mas profundos). Distinto de
 * [BalanceHierarchyColors], que da el color del propio card segun SU
 * profundidad: este objeto da colores para los segmentos DENTRO de
 * una misma barra, por eso deben ser obviamente distintos entre si.
 *
 * Niveles 1 y 2 usan los colores fijos de la paleta (child, grandChild).
 * Niveles 3+ se generan rotando el matiz (hue) de grandChild en pasos
 * de 40 grados, garantizando colores nuevos y distinguibles sin tener
 * que definir manualmente un color por cada nivel posible.
 */
object BalanceSegmentColors {
    private const val HUE_STEP_DEGREES = 40f

    @Composable
    fun forLevel(level: Int): Color {
        val palette = LocalFondlyHierarchy.current
        return when (level) {
            1 -> palette.child
            2 -> palette.grandChild
            else -> rotateHue(palette.grandChild, HUE_STEP_DEGREES * (level - 2))
        }
    }

    private fun rotateHue(color: Color, degrees: Float): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hsv[0] = (hsv[0] + degrees) % 360f
        return Color(android.graphics.Color.HSVToColor(hsv))
    }
}