package lat.virgotp.fondly.balances.form

/**
 * Errores de formulario como TIPO, no como texto: la UI mapea cada caso a
 * stringResource. Los ViewModels nunca deciden texto visible (RNF-010).
 */
enum class BalanceFormError {
    EMPTY_NAME,
    INVALID_AMOUNT,
    MISSING_RENEWAL_DATE,
    INVALID_THRESHOLD,
    PARENT_NOT_FOUND,
    PARENT_EXCEEDED,
    CYCLE_NOT_ALLOWED,
    BALANCE_NOT_FOUND,
    UNKNOWN
}
