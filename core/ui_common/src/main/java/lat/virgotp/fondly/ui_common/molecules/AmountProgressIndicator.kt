package lat.virgotp.fondly.ui_common.molecules

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
import lat.virgotp.fondly.ui_common.util.FondlyMoney

/**
 * Indicador de monto: disponible vs objetivo, con barra animada.
 * Montos formateados en pesos mexicanos (MXN).
 */
@Composable
fun AmountProgressIndicator(
    available: Double,
    target: Double,
    modifier: Modifier = Modifier
) {
    val fraction = if (target > 0) (available / target).coerceIn(0.0, 1.0).toFloat() else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = FondlyMoney.formatMXN(available),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "de ${FondlyMoney.formatMXN(target)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(FondlySpacing.sm))
        FondlyProgressBar(fraction = fraction)
    }
}