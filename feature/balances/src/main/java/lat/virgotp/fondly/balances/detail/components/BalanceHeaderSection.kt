package lat.virgotp.fondly.balances.detail.components

import java.math.BigDecimal
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.feature.balances.R
import lat.virgotp.fondly.ui_common.molecules.BalanceHeader
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.util.FondlyMoney

/**
 * Encabezado del detalle: nombre + icono de jerarquia + monto disponible.
 * Vive fuera de cualquier card (fondo transparente) por diseño.
 */
@Composable
fun BalanceHeaderSection(
    name: String,
    hierarchyLabel: String,
    icon: ImageVector,
    hierarchyColor: Color,
    available: BigDecimal,
    modifier: Modifier = Modifier
) {
    val currency = FondlyCurrencyController.rememberCurrency()
    Column(modifier.fillMaxWidth()) {
        BalanceHeader(
            name = name,
            hierarchyLabel = hierarchyLabel,
            icon = icon,
            hierarchyColor = hierarchyColor,
            onEditClick = null
        )
        Text(
            stringResource(R.string.detail_available),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = FondlySpacing.md)
        )
        Text(
            FondlyMoney.format(available, currency),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}