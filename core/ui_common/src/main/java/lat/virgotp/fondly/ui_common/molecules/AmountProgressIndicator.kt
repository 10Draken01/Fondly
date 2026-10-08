package lat.virgotp.fondly.ui_common.molecules

import java.math.RoundingMode
import java.math.BigDecimal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lat.virgotp.fondly.ui_common.atoms.FondlyProgressBar
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.util.FondlyMoney

/**
 * Indicador de monto: disponible vs objetivo, con barra animada.
 * Montos formateados en pesos mexicanos (MXN).
 */
@Composable
fun AmountProgressIndicator(
    available: BigDecimal,
    target: BigDecimal,
    modifier: Modifier = Modifier
) {
    val currency = FondlyCurrencyController.rememberCurrency()
    val fraction = if (target.signum() > 0) available.divide(target, 6, RoundingMode.HALF_EVEN).toFloat().coerceIn(0f, 1f) else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = FondlyMoney.format(available, currency),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "de ${FondlyMoney.format(target, currency)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(FondlySpacing.sm))
        FondlyProgressBar(fraction = fraction)
    }
}