package lat.virgotp.fondly.ui_common.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Monedas soportadas (solo representación visual — NO conversión).
 *
 * REGLA DE NEGOCIO: la moneda es un atributo DESCRIPTIVO/ILUSTRATIVO.
 * Seleccionar una moneda jamás transforma el valor numérico del saldo:
 * `amount` (BigDecimal) y `currency` son conceptos independientes y no
 * existe ninguna conversión de divisas en el proyecto.
 */
enum class FondlyCurrency(val code: String, val symbol: String) {
    MXN("MXN", "$"),
    USD("USD", "US$"),
    EUR("EUR", "€"),
    GBP("GBP", "£"),
    JPY("JPY", "¥"),
    CAD("CAD", "CA$"),
    ARS("ARS", "AR$"),
    COP("COP", "CO$"),
    CLP("CLP", "CL$"),
    PEN("PEN", "S/"),
    BRL("BRL", "R$");

    companion object {
        fun fromCode(code: String?): FondlyCurrency =
            entries.firstOrNull { it.code == code } ?: MXN
    }
}

/**
 * Controlador global de moneda ilustrativa (misma arquitectura que
 * FondlyThemeController): singleton persistido en SharedPreferences,
 * sin DI. No toca dominio ni persistencia financiera.
 */
object FondlyCurrencyController {

    private const val PREFS = "fondly_settings"
    private const val KEY_CURRENCY = "currency_code"

    private val _currency = MutableStateFlow(FondlyCurrency.MXN)
    val currency: StateFlow<FondlyCurrency> = _currency.asStateFlow()

    fun init(context: Context) {
        _currency.value = FondlyCurrency.fromCode(
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_CURRENCY, FondlyCurrency.MXN.code)
        )
    }

    fun setCurrency(context: Context, newCurrency: FondlyCurrency) {
        _currency.value = newCurrency
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_CURRENCY, newCurrency.code).apply()
    }

    /** Moneda ilustrativa actual, reactiva. Uso exclusivo en UI. */
    @Composable
    fun rememberCurrency(): FondlyCurrency {
        val current by currency.collectAsState()
        return current
    }
}
