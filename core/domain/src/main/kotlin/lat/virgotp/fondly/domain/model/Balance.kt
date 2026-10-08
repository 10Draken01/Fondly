package lat.virgotp.fondly.domain.model

import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.time.Instant

/**
 * Escala y redondeo monetarios oficiales del dominio (ADR-0003):
 * todo dinero es BigDecimal con scale = 2 y RoundingMode.HALF_EVEN.
 * Comparaciones SIEMPRE con compareTo(), nunca equals().
 */
fun BigDecimal.money(): BigDecimal = setScale(2, RoundingMode.HALF_EVEN)

data class Balance(
    val id: Long = 0L,
    val name: String,
    val targetAmount: BigDecimal,
    val available: BigDecimal,
    val periodicity: Periodicity = Periodicity.NONE,
    val renewalDate: LocalDate? = null,
    val parentBalanceId: Long? = null,
    val type: BalanceType = BalanceType.REGULAR,
    val rolloverStrategy: RolloverStrategy = RolloverStrategy.RESET,
    val rebalanceStrategy: RebalanceStrategy = RebalanceStrategy.EVEN,
    val allowOverdraft: Boolean = false,
    val isActive: Boolean = true,
    val description: String? = null,
    val notificationThreshold: Int? = null,
    val createdAt: Instant
) {
    init {
        require(name.isNotBlank()) { "El nombre del saldo no puede estar vacío" }
        require(targetAmount.money() >= BigDecimal.ZERO) { "El monto objetivo no puede ser negativo" }
        notificationThreshold?.let {
            require(it in 0..100) { "El umbral de notificación debe estar entre 0 y 100" }
        }
    }

    /** Copia con montos normalizados a la escala monetaria oficial. */
    fun normalized(): Balance = copy(
        targetAmount = targetAmount.money(),
        available = available.money()
    )
}
