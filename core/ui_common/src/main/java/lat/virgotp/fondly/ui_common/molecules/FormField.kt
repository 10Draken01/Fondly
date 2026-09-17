package lat.virgotp.fondly.ui_common.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import lat.virgotp.fondly.ui_common.atoms.FondlyTextField
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/**
 * Campo de formulario con mensaje de error animado.
 * La etiqueta flota dentro del campo (FondlyTextField).
 */
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FondlyTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = errorMessage != null,
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = imeAction
        )
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = FondlySpacing.sm, top = FondlySpacing.xs)
            )
        }
    }
}