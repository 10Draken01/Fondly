package lat.virgotp.fondly.ui_common.atoms

import java.math.BigDecimal
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.uicommon.R
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.util.FondlyMoney

/**
 * Texto "actual de total", ej. "$800.00 de $1,000.00".
 * El monto actual resalta; el total queda en tono neutro.
 */
@Composable
fun AmountOfTotalText(
    current: BigDecimal,
    total: BigDecimal,
    modifier: Modifier = Modifier,
    currentColor: Color = MaterialTheme.colorScheme.onSurface,
    totalColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val currency = FondlyCurrencyController.rememberCurrency()
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = currentColor, fontWeight = FontWeight.SemiBold)) {
            append(FondlyMoney.format(current, currency))
        }
        withStyle(SpanStyle(color = totalColor)) {
            append(" ")
            append(stringResource(R.string.amount_of_total_connector))
            append(" ")
            append(FondlyMoney.format(total, currency))
        }
    }
    Text(text = text, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}