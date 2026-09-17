package lat.virgotp.fondly.ui_common.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================
// Paleta "Premium Gold" — Fondly
// Claro: marfil calido + dorado profundo (tipo private banking)
// Oscuro: negro calido (no puro) + dorado brillante
// ============================================================

val Gold900 = Color(0xFF4A3803)
val Gold700 = Color(0xFF8A6D1F)
val Gold500 = Color(0xFFB08D2E)
val Gold300 = Color(0xFFD4AF37)

val GoldGradientStart = Color(0xFFEACD6F)
val GoldGradientEnd = Color(0xFFC79A2F)

val Ivory = Color(0xFFFBF8F2)
val Ink = Color(0xFF221F19)

val Bronze = Color(0xFF8C5A36)
val SlateBlue = Color(0xFF5B6B85)

val BronzeDark = Color(0xFFD89B6E)
val SlateBlueDark = Color(0xFFA9B8D4)
val SuccessGreen = Color(0xFF2E8B57)

internal val FondlyLightColors = lightColorScheme(
    primary = Gold700,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF7EBCB),
    onPrimaryContainer = Gold900,
    inversePrimary = Color(0xFFE4BC55),
    secondary = Color(0xFF6E6552),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9E3D5),
    onSecondaryContainer = Color(0xFF282215),
    tertiary = Color(0xFF7D5836),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF6DCC2),
    onTertiaryContainer = Color(0xFF3A2508),
    background = Ivory,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF1EBDD),
    onSurfaceVariant = Color(0xFF5D5647),
    surfaceTint = Gold700,
    outline = Color(0xFFCBC2AE),
    outlineVariant = Color(0xFFE4DCCB),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    scrim = Color.Black
)

internal val FondlyDarkColors = darkColorScheme(
    primary = Color(0xFFE4BC55),
    onPrimary = Gold900,
    primaryContainer = Color(0xFF5A440A),
    onPrimaryContainer = Color(0xFFF7E3AC),
    inversePrimary = Gold700,
    secondary = Color(0xFFD3C7A8),
    onSecondary = Color(0xFF38311F),
    secondaryContainer = Color(0xFF4F4632),
    onSecondaryContainer = Color(0xFFE9E3D2),
    tertiary = Color(0xFFE9B98F),
    onTertiary = Color(0xFF452B0D),
    tertiaryContainer = Color(0xFF5D3D1D),
    onTertiaryContainer = Color(0xFFF6DCC2),
    background = Color(0xFF131211),   // negro calido, no puro
    onBackground = Color(0xFFECE8DE),
    surface = Color(0xFF1A1815),
    onSurface = Color(0xFFECE8DE),
    surfaceVariant = Color(0xFF2B2822),
    onSurfaceVariant = Color(0xFFCBC4B4),
    surfaceTint = Color(0xFFE4BC55),
    outline = Color(0xFF4B463C),
    outlineVariant = Color(0xFF37332C),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    scrim = Color.Black
)

/**
 * Colores para distinguir la jerarquia de saldos:
 * base (dorado) -> apartado (bronce) -> sub-apartado (azul pizarra).
 */
@Immutable
data class FondlyHierarchyPalette(
    val base: Color,
    val child: Color,
    val grandChild: Color
)

val LocalFondlyHierarchy = staticCompositionLocalOf {
    FondlyHierarchyPalette(base = Gold700, child = Bronze, grandChild = SlateBlue)
}

internal val LightHierarchy =
    FondlyHierarchyPalette(base = Gold700, child = Bronze, grandChild = SlateBlue)

internal val DarkHierarchy =
    FondlyHierarchyPalette(base = Color(0xFFE4BC55), child = BronzeDark, grandChild = SlateBlueDark)