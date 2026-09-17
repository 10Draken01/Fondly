package lat.virgotp.fondly.ui_common.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Formato de dinero en pesos mexicanos (MXN).
 * Ejemplo: "$1,250.50"
 *
 * Nota: NumberFormat no es thread-safe; usar solo en el hilo principal (UI).
 */
object FondlyMoney {

    private val mxnFormat: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    fun formatMXN(amount: Double): String = mxnFormat.format(amount)
}