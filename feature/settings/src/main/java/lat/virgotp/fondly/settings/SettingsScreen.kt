package lat.virgotp.fondly.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import lat.virgotp.fondly.ui_common.atoms.GlassSurface
import lat.virgotp.fondly.ui_common.theme.*

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val themeMode by FondlyThemeController.mode.collectAsState()
    val currentLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium, color = scheme.onBackground)
        Spacer(Modifier.height(FondlySpacing.lg))

        GlassSurface {
            Column(Modifier.padding(FondlySpacing.lg)) {
                Text("Tema", style = MaterialTheme.typography.titleMedium, color = scheme.onSurface)
                Spacer(Modifier.height(FondlySpacing.sm))
                FondlyThemeMode.entries.forEach { mode ->
                    val label = when (mode) {
                        FondlyThemeMode.SYSTEM -> "Seguir sistema"
                        FondlyThemeMode.LIGHT -> "Claro"
                        FondlyThemeMode.DARK -> "Oscuro"
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = FondlySpacing.xs)
                    ) {
                        RadioButton(
                            selected = themeMode == mode,
                            onClick = { FondlyThemeController.setMode(context, mode) },
                            colors = RadioButtonDefaults.colors(selectedColor = scheme.primary)
                        )
                        Text(label, style = MaterialTheme.typography.bodyLarge, color = scheme.onSurface)
                    }
                }
            }
        }

        Spacer(Modifier.height(FondlySpacing.md))

        GlassSurface {
            Column(Modifier.padding(FondlySpacing.lg)) {
                Text("Idioma", style = MaterialTheme.typography.titleMedium, color = scheme.onSurface)
                Spacer(Modifier.height(FondlySpacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(FondlySpacing.sm)) {
                    FilterChip(
                        selected = !currentLanguage.startsWith("en"),
                        onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es")) },
                        label = { Text("Español") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = scheme.primary.copy(alpha = 0.18f),
                            selectedLabelColor = scheme.primary
                        )
                    )
                    FilterChip(
                        selected = currentLanguage.startsWith("en"),
                        onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en")) },
                        label = { Text("English") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = scheme.primary.copy(alpha = 0.18f),
                            selectedLabelColor = scheme.primary
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(FondlySpacing.md))
        Text(
            "Fondly · Finanzas personales",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}