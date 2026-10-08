package lat.virgotp.fondly.ui_common.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/**
 * Componentes de formulario específicos de Fondly.
 *
 * Patrón visual: etiqueta SOBRE el campo (mejor legibilidad y accesibilidad
 * que el label flotante genérico), placeholder de ejemplo, texto de apoyo,
 * mensaje de error con icono (no solo color — accesibilidad), y teclado
 * acorde al tipo de dato con navegación ImeAction.
 */

@Composable
private fun formFieldColors() = run {
    val scheme = MaterialTheme.colorScheme
    TextFieldDefaults.colors(
        focusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.55f),
        unfocusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.35f),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        cursorColor = scheme.primary,
        errorCursorColor = scheme.error,
        focusedTextColor = scheme.onSurface,
        unfocusedTextColor = scheme.onSurface,
        disabledContainerColor = scheme.surfaceVariant.copy(alpha = 0.25f),
        disabledTextColor = scheme.onSurfaceVariant.copy(alpha = 0.6f),
        errorContainerColor = scheme.error.copy(alpha = 0.08f),
        focusedPlaceholderColor = scheme.onSurfaceVariant.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = scheme.onSurfaceVariant.copy(alpha = 0.5f),
        disabledPlaceholderColor = scheme.onSurfaceVariant.copy(alpha = 0.4f),
        focusedLeadingIconColor = scheme.primary,
        unfocusedLeadingIconColor = scheme.onSurfaceVariant,
        errorLeadingIconColor = scheme.error,
        disabledLeadingIconColor = scheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
}

private val fieldShape = RoundedCornerShape(16.dp)

/** Etiqueta sobre el campo + mensaje de error o texto de apoyo animado. */
@Composable
fun FormFieldScaffold(
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    supportingText: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = FondlySpacing.xs, start = FondlySpacing.xs)
        )
        content()
        AnimatedVisibility(
            visible = errorMessage != null || (errorMessage == null && supportingText != null),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = FondlySpacing.xs, top = FondlySpacing.xs)
            ) {
                if (errorMessage != null) {
                    Icon(
                        Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.height(14.dp)
                    )
                    Spacer(Modifier.width(FondlySpacing.xs))
                }
                Text(
                    text = errorMessage ?: supportingText.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (errorMessage != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

/** Campo de texto de formulario (nombre, descripción). */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    imeAction: ImeAction = ImeAction.Next
) {
    val focusManager = LocalFocusManager.current
    FormFieldScaffold(label = label, errorMessage = errorMessage, supportingText = supportingText, modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder?.let { { Text(it) } },
            isError = errorMessage != null,
            singleLine = singleLine,
            shape = fieldShape,
            colors = formFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Campo monetario: símbolo de moneda + teclado decimal. NO convierte valores. */
@Composable
fun FormAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    FormFieldScaffold(label = label, errorMessage = errorMessage, supportingText = supportingText, modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder?.let { { Text(it) } },
            isError = errorMessage != null,
            singleLine = true,
            shape = fieldShape,
            colors = formFieldColors(),
            leadingIcon = {
                Text(
                    text = currencySymbol,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = FondlySpacing.sm)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = imeAction
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Selector de opciones de formulario (dropdown anclado, API actual). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdownField(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    FormFieldScaffold(label = label, supportingText = supportingText, modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = it }
        ) {
            TextField(
                value = options.getOrNull(selectedIndex).orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                singleLine = true,
                shape = fieldShape,
                colors = formFieldColors(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = { onSelected(index); expanded = false },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

/** Campo de fecha de formulario: abre DatePickerDialog del sistema. */
@Composable
fun FormDateField(
    label: String,
    date: kotlinx.datetime.LocalDate?,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    errorMessage: String? = null,
    supportingText: String? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    FormFieldScaffold(label = label, errorMessage = errorMessage, supportingText = supportingText, modifier = modifier) {
        Box {
            TextField(
                value = date?.toString().orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                singleLine = true,
                shape = fieldShape,
                colors = formFieldColors(),
                placeholder = { Text("AAAA-MM-DD") },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            if (enabled) {
                Box(
                    Modifier
                        .matchParentSize()
                        .clickable {
                            val now = java.util.Calendar.getInstance()
                            android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    onDateChange(kotlinx.datetime.LocalDate(year, month + 1, dayOfMonth))
                                },
                                date?.year ?: now.get(java.util.Calendar.YEAR),
                                ((date?.month?.ordinal?.plus(1)) ?: (now.get(java.util.Calendar.MONTH) + 1)) - 1,
                                date?.day ?: now.get(java.util.Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                )
            }
        }
    }
}

/** Fila de checkbox con etiqueta y descripción (acción semántica clara). */
@Composable
fun FormCheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = FondlySpacing.xs)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(Modifier.width(FondlySpacing.sm))
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            if (supportingText != null) {
                Text(
                    supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Fila de switch con etiqueta (estado on/off). */
@Composable
fun FormSwitchRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = FondlySpacing.xs)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(Modifier.width(FondlySpacing.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (checked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/** Sección del formulario: título + contenido en una superficie PLANA. */
@Composable
fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = scheme.primary,
            modifier = Modifier.padding(start = FondlySpacing.xs, bottom = FondlySpacing.sm)
        )
        // Los formularios usan una superficie simple con borde fino: GlassSurface
        // (halo, grano, sheen) está reservado para cards destacadas.
        androidx.compose.material3.Surface(
            color = scheme.surface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, scheme.outlineVariant)
        ) {
            Column(Modifier.padding(FondlySpacing.lg)) { content() }
        }
    }
}
