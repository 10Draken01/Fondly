package lat.virgotp.fondly.ui_common.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FondlyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val scheme = MaterialTheme.colorScheme
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        leadingIcon = leadingIcon,
        singleLine = true,
        readOnly = readOnly,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = imeAction
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.55f),
            unfocusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.35f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedLabelColor = scheme.primary,
            unfocusedLabelColor = scheme.onSurfaceVariant,
            cursorColor = scheme.primary,
            errorCursorColor = scheme.error,
            focusedLeadingIconColor = scheme.primary,
            unfocusedLeadingIconColor = scheme.onSurfaceVariant,
            errorLeadingIconColor = scheme.error,
            errorContainerColor = scheme.error.copy(alpha = 0.08f),
            focusedTextColor = scheme.onSurface,
            unfocusedTextColor = scheme.onSurface,
            disabledContainerColor = scheme.surfaceVariant.copy(alpha = 0.25f),
            disabledTextColor = scheme.onSurfaceVariant.copy(alpha = 0.6f),
            disabledLabelColor = scheme.onSurfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}