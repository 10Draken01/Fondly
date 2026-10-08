package lat.virgotp.fondly.ui_common.util

import java.math.BigDecimal
import java.util.Currency
import java.text.NumberFormat
import java.util.Locale
import lat.virgotp.fondly.ui_common.theme.FondlyCurrency

/**
 * Formato de dinero. REGLA PERMANENTE: único punto de formateo a texto.
 *
 * La moneda es SOLO descriptiva (ver FondlyCurrency): cambia el símbolo
 * mostrado, jamás el valor numérico. No existe conversión de divisas.
 * El dominio maneja dinero en BigDecimal (escala 2, HALF_EVEN — ADR-0003);
 * prohibido formatear dinero con Double/Float.
 *
 * Nota: NumberFormat no es thread-safe; usar solo en el hilo principal (UI).
 */
object FondlyMoney {

    private val locale = Locale("es", "MX")

    private fun formatterFor(currency: FondlyCurrency): NumberFormat =
        NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = Currency.getInstance(currency.code)
        }

    /** Formatea [amount] con el símbolo de [currency], sin modificar el valor. */
    fun format(amount: BigDecimal, currency: FondlyCurrency): String =
        formatterFor(currency).format(amount)
}
