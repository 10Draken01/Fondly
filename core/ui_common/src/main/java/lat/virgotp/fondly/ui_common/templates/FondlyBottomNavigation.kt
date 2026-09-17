package lat.virgotp.fondly.ui_common.templates

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class FondlySection(val label: String, val icon: ImageVector, val route: String) {
    BALANCES("Balances", Icons.Default.Home, "balances"),
    BALANCE_SECTIONS("Balance Sections", Icons.AutoMirrored.Filled.List, "balance_sections"),
    SETTINGS("Settings", Icons.Default.Settings, "settings")
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
            NavigationBarItem(
                selected = currentRoute == section.route,
                onClick = { onNavigate(section.route) },
                icon = { Icon(section.icon, contentDescription = section.label) },
                label = { Text(section.label, style = MaterialTheme.typography.labelMedium) },
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