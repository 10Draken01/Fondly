package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import lat.virgotp.fondly.uicommon.R

/**
 * Dialogo de confirmacion generico para acciones importantes o
 * irreversibles (desactivar, renovar, eliminar, etc.).
 * No se auto-cierra: quien lo usa decide en onConfirm/onDismiss
 * si cambia su estado "show".
 */
@Composable
fun ConfirmationDialog(
    show: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = stringResource(R.string.action_confirm),
    dismissLabel: String = stringResource(R.string.action_cancel),
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    icon: ImageVector? = null
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let { vector -> { Icon(vector, contentDescription = null, tint = confirmColor) } },
        title = { Text(title) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = confirmColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }
        }
    )
}