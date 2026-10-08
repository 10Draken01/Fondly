package lat.virgotp.fondly.balances.form

import lat.virgotp.fondly.domain.exception.BalanceCycleException
import lat.virgotp.fondly.domain.exception.ParentBalanceExceededException
import lat.virgotp.fondly.domain.exception.ParentBalanceNotFoundException

/** Mapea excepciones de dominio a errores tipados de formulario (sin texto). */
fun Throwable.toFormError(): BalanceFormError = when (this) {
    is ParentBalanceExceededException -> BalanceFormError.PARENT_EXCEEDED
    is ParentBalanceNotFoundException -> BalanceFormError.PARENT_NOT_FOUND
    is BalanceCycleException -> BalanceFormError.CYCLE_NOT_ALLOWED
    is IllegalArgumentException -> BalanceFormError.UNKNOWN
    else -> BalanceFormError.UNKNOWN
}
