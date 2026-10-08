package lat.virgotp.fondly.ui_common.molecules

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import kotlinx.datetime.LocalDate
import lat.virgotp.fondly.ui_common.atoms.FondlyTextField

/** Campo de fecha con DatePickerDialog del framework (sin APIs experimentales). */
@Composable
fun FondlyDateField(
    label: String,
    date: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxWidth()) {
        FondlyTextField(
            value = date?.toString().orEmpty(),
            onValueChange = {},
            label = label,
            readOnly = true,
            enabled = enabled,
            leadingIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
        if (enabled) {
            Box(
                Modifier.fillMaxWidth().clickable {
                    val now = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            onDateChange(LocalDate(year, month + 1, dayOfMonth))
                        },
                        date?.year ?: now.get(Calendar.YEAR),
                        (date?.monthNumber ?: (now.get(Calendar.MONTH) + 1)) - 1,
                        date?.dayOfMonth ?: now.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            )
        }
    }
}