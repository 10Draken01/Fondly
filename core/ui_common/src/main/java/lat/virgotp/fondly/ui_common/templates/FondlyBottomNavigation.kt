package lat.virgotp.fondly.ui_common.templates

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.uicommon.R
import lat.virgotp.fondly.ui_common.navigation.FondlyRoutes

enum class FondlySection(@param:StringRes val labelRes: Int, val icon: ImageVector, val route: String) {
    BALANCES(R.string.nav_balances, Icons.Default.Home, FondlyRoutes.BALANCES),
    BALANCE_SECTIONS(R.string.nav_sections, Icons.AutoMirrored.Filled.List, FondlyRoutes.BALANCE_SECTIONS),
    SETTINGS(R.string.nav_settings, Icons.Default.Settings, FondlyRoutes.SETTINGS)
}

@Composable
fun FondlyBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    NavigationBar(modifier = modifier, containerColor = scheme.surface.copy(alpha = 0.94f), tonalElevation = 8.dp) {
        FondlySection.entries.forEach { section ->
            val label = stringResource(section.labelRes)
            NavigationBarItem(
                selected = currentRoute == section.route,
                onClick = { onNavigate(section.route) },
                icon = { Icon(section.icon, contentDescription = label) },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = scheme.onPrimary,
                    selectedTextColor = scheme.primary,
                    indicatorColor = scheme.primary,
                    unselectedIconColor = scheme.onSurfaceVariant,
                    unselectedTextColor = scheme.onSurfaceVariant
                )
            )
        }
    }
}