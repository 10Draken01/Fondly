package lat.virgotp.fondly.ui_common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Tema Fondly. Detecta automaticamente el modo claro/oscuro del sistema.
 * Uso en la app:
 *   setContent { FondlyTheme { App() } }
 */
@Composable
fun FondlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FondlyDarkColors else FondlyLightColors

    CompositionLocalProvider(
        LocalFondlyHierarchy provides if (darkTheme) DarkHierarchy else LightHierarchy
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FondlyTypography,
            shapes = FondlyShapes,
            content = content
        )
    }
}