package lat.virgotp.fondly.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class Balance(
    val id: Long = 0L,
    val name: String,
    val targetAmount: Double,
    val available: Double,
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
        require(targetAmount >= 0) { "El monto objetivo no puede ser negativo" }
        notificationThreshold?.let {
            require(it in 0..100) { "El umbral de notificación debe estar entre 0 y 100" }
        }
    }
}