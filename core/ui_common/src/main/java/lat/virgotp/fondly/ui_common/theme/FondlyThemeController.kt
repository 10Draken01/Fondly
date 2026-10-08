package lat.virgotp.fondly.ui_common.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FondlyThemeMode { SYSTEM, LIGHT, DARK }

/** Controlador global de tema (RF-026), singleton sin DI. Persistido en SharedPreferences. */
object FondlyThemeController {

    private const val PREFS = "fondly_settings"
    private const val KEY_THEME = "theme_mode"

    private val _mode = MutableStateFlow(FondlyThemeMode.SYSTEM)
    val mode: StateFlow<FondlyThemeMode> = _mode.asStateFlow()

    fun init(context: Context) {
        _mode.value = when (
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_THEME, "SYSTEM")
        ) {
            "LIGHT" -> FondlyThemeMode.LIGHT
            "DARK" -> FondlyThemeMode.DARK
            else -> FondlyThemeMode.SYSTEM
        }
    }

    fun setMode(context: Context, newMode: FondlyThemeMode) {
        _mode.value = newMode
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_THEME, newMode.name).apply()
    }

    @Composable
    fun isDarkTheme(): Boolean {
        val current by mode.collectAsState()
        return when (current) {
            FondlyThemeMode.SYSTEM -> isSystemInDarkTheme()
            FondlyThemeMode.LIGHT -> false
            FondlyThemeMode.DARK -> true
        }
    }
}