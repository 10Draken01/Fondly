package lat.virgotp.fondly.ui_common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

    // Integración con system bars (edge-to-edge): las barras son transparentes
    // (enableEdgeToEdge en la Activity) y aquí sincronizamos el CONTRASTE de los
    // iconos (hora, batería...) con el tema actual de la app — incluyendo cuando
    // el usuario cambia el tema sin reiniciar. El contenido de la app se dibuja
    // detrás; el gradiente de fondo proporciona el color y los iconos claros u
    // oscuros garantizan la legibilidad.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = !darkTheme
        }
    }

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