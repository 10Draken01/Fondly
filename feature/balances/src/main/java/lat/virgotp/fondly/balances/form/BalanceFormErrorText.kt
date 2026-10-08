package lat.virgotp.fondly.balances.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.feature.balances.R

/** Texto localizado de un error tipado (el ViewModel nunca genera texto). */
@Composable
fun balanceFormErrorText(error: BalanceFormError): String = stringResource(
    when (error) {
        BalanceFormError.EMPTY_NAME -> R.string.error_name_empty
        BalanceFormError.INVALID_AMOUNT -> R.string.error_amount_invalid
        BalanceFormError.MISSING_RENEWAL_DATE -> R.string.error_renewal_date_missing
        BalanceFormError.INVALID_THRESHOLD -> R.string.error_threshold_invalid
        BalanceFormError.PARENT_NOT_FOUND -> R.string.error_parent_not_found
        BalanceFormError.PARENT_EXCEEDED -> R.string.error_parent_exceeded
        BalanceFormError.CYCLE_NOT_ALLOWED -> R.string.error_cycle_not_allowed
        BalanceFormError.BALANCE_NOT_FOUND -> R.string.error_balance_not_found
        BalanceFormError.UNKNOWN -> R.string.error_unknown
    }
)
