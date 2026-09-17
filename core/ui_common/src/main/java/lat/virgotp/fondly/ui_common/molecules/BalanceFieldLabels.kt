package lat.virgotp.fondly.ui_common.molecules

import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy

/** Etiquetas en español para los enums del dominio (RF-039). */
object BalanceFieldLabels {

    fun periodicityLabel(p: Periodicity): String = when (p) {
        Periodicity.NONE -> "Sin renovación"
        Periodicity.DAILY -> "Diaria"
        Periodicity.WEEKLY -> "Semanal"
        Periodicity.BIWEEKLY -> "Quincenal"
        Periodicity.MONTHLY -> "Mensual"
        Periodicity.YEARLY -> "Anual"
        Periodicity.CUSTOM -> "Personalizada"
    }

    fun rolloverLabel(s: RolloverStrategy): String = when (s) {
        RolloverStrategy.RESET -> "Restablecer (vuelve a 100%)"
        RolloverStrategy.ACCUMULATE -> "Acumular (conserva sobrante)"
        RolloverStrategy.TRANSFER_TO_SAVINGS -> "Transferir a ahorro"
    }

    fun rebalanceLabel(s: RebalanceStrategy): String = when (s) {
        RebalanceStrategy.EVEN -> "Uniforme (EVEN)"
        RebalanceStrategy.FRONT_LOADED -> "Anticipado"
        RebalanceStrategy.CUSTOM -> "Personalizado"
    }

    fun typeLabel(t: BalanceType): String = when (t) {
        BalanceType.REGULAR -> "Regular"
        BalanceType.SAVINGS -> "Ahorro"
    }

    val periodicities = Periodicity.entries.toList()
    val rollovers = RolloverStrategy.entries.toList()
    val rebalances = RebalanceStrategy.entries.toList()
    val types = BalanceType.entries.toList()
}