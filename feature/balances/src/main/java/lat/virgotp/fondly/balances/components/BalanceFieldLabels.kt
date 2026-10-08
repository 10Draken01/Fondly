package lat.virgotp.fondly.balances.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy
import lat.virgotp.fondly.feature.balances.R

/**
 * Etiquetas localizadas para los enums del dominio (RF-039, RNF-010).
 * Composables: resuelven strings.xml ES/EN; el dominio jamás produce texto de UI.
 */
object BalanceFieldLabels {

    val periodicities = Periodicity.entries.toList()
    val rollovers = RolloverStrategy.entries.toList()
    val rebalances = RebalanceStrategy.entries.toList()
    val types = BalanceType.entries.toList()

    @Composable
    fun periodicityLabel(p: Periodicity): String = stringResource(
        when (p) {
            Periodicity.NONE -> R.string.periodicity_none
            Periodicity.DAILY -> R.string.periodicity_daily
            Periodicity.WEEKLY -> R.string.periodicity_weekly
            Periodicity.BIWEEKLY -> R.string.periodicity_biweekly
            Periodicity.MONTHLY -> R.string.periodicity_monthly
            Periodicity.YEARLY -> R.string.periodicity_yearly
            Periodicity.CUSTOM -> R.string.periodicity_custom
        }
    )

    @Composable
    fun rolloverLabel(s: RolloverStrategy): String = stringResource(
        when (s) {
            RolloverStrategy.RESET -> R.string.rollover_reset
            RolloverStrategy.ACCUMULATE -> R.string.rollover_accumulate
            RolloverStrategy.TRANSFER_TO_SAVINGS -> R.string.rollover_transfer_savings
        }
    )

    @Composable
    fun rebalanceLabel(s: RebalanceStrategy): String = stringResource(
        when (s) {
            RebalanceStrategy.EVEN -> R.string.rebalance_even
            RebalanceStrategy.FRONT_LOADED -> R.string.rebalance_front_loaded
            RebalanceStrategy.CUSTOM -> R.string.rebalance_custom
        }
    )

    @Composable
    fun typeLabel(t: BalanceType): String = stringResource(
        when (t) {
            BalanceType.REGULAR -> R.string.balance_type_regular
            BalanceType.SAVINGS -> R.string.balance_type_savings
        }
    )
}
