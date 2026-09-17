package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import lat.virgotp.fondly.ui_common.util.FondlyMoney

/**
 * Texto "actual de total", ej. "$800.00 de $1,000.00".
 * El monto actual resalta; el total queda en tono neutro.
 */
@Composable
fun AmountOfTotalText(
    current: Double,
    total: Double,
    modifier: Modifier = Modifier,
    currentColor: Color = MaterialTheme.colorScheme.onSurface,
    totalColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = currentColor, fontWeight = FontWeight.SemiBold)) {
            append(FondlyMoney.formatMXN(current))
        }
        withStyle(SpanStyle(color = totalColor)) {
            append(" de ")
            append(FondlyMoney.formatMXN(total))
        }
    }
    Text(text = text, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}