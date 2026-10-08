package lat.virgotp.fondly.ui_common.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icono representativo segun el nivel de jerarquia del saldo.
 * Requiere la dependencia material-icons-extended.
 */
object BalanceHierarchyIcons {
    fun forDepth(depth: Int): ImageVector = when {
        depth <= 0 -> Icons.Filled.AccountBalanceWallet
        depth == 1 -> Icons.Filled.Savings
        else -> Icons.Filled.Flag
    }
}