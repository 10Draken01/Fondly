package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.port.BalanceRepository
import javax.inject.Inject
import kotlin.time.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.datetime.plus
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RolloverStrategy
import lat.virgotp.fondly.domain.model.money

class ProcessBalanceRenewalsUseCase @Inject constructor(private val repository: BalanceRepository) {
    suspend operator fun invoke(): Result<Int> = try {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val balances = repository.getAllBalances().first()
        var renewed = 0
        for (b in balances) {
            if (!b.isActive || b.periodicity == Periodicity.NONE || b.renewalDate == null) continue
            var next = b.renewalDate
            var available = b.available.money()
            var changed = false
            while (next!! <= today) {
                available = when (b.rolloverStrategy) {
                    RolloverStrategy.RESET -> b.targetAmount.money()
                    RolloverStrategy.ACCUMULATE ->
                        (available + (b.targetAmount - available).coerceAtLeast(java.math.BigDecimal.ZERO)).money()
                    RolloverStrategy.TRANSFER_TO_SAVINGS -> b.targetAmount.money()
                }
                next = nextDate(next, b.periodicity)
                changed = true
            }
            if (changed) {
                repository.update(b.copy(available = available.money(), renewalDate = next))
                renewed++
            }
        }
        Result.success(renewed)
    } catch (e: Exception) { Result.failure(e) }

    private fun nextDate(date: LocalDate, p: Periodicity): LocalDate = when (p) {
        Periodicity.DAILY -> date.plus(DatePeriod(days = 1))
        Periodicity.WEEKLY -> date.plus(DatePeriod(days = 7))
        Periodicity.BIWEEKLY -> date.plus(DatePeriod(days = 14))
        Periodicity.MONTHLY -> date.plus(DatePeriod(months = 1))
        Periodicity.YEARLY -> date.plus(DatePeriod(years = 1))
        Periodicity.CUSTOM, Periodicity.NONE -> date.plus(DatePeriod(months = 1))
    }
}
