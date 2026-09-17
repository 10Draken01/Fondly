package lat.virgotp.fondly.ui_common.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lat.virgotp.fondly.uicommon.R

// Playfair Display: titulares con caracter premium (serif)
private val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_medium, FontWeight.Medium),
    Font(R.font.playfair_display_semibold, FontWeight.SemiBold),
    Font(R.font.playfair_display_bold, FontWeight.Bold)
)

// Inter: texto funcional, limpio y moderno (sans)
private val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

val FondlyTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp, lineHeight = 50.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp, lineHeight = 42.sp
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.Medium,
        fontSize = 30.sp, lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.Medium,
        fontSize = 20.sp, lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlayfairDisplay, fontWeight = FontWeight.Medium,
        fontSize = 20.sp, lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 1.25.sp
    )
)