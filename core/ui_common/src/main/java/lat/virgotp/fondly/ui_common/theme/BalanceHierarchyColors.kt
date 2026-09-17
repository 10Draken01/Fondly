package lat.virgotp.fondly.ui_common.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BalanceHierarchyColors {
    @Composable
    fun forDepth(depth: Int): Color {
        val palette = LocalFondlyHierarchy.current
        return when {
            depth <= 0 -> palette.base
            depth == 1 -> palette.child
            else -> palette.grandChild
        }
    }

    @Composable
    fun labelForDepth(depth: Int): String = when {
        depth <= 0 -> "Saldo base"
        depth == 1 -> "Apartado"
        else -> "Sub-apartado"
    }
}