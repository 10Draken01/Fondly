package lat.virgotp.fondly.domain.exception

class ParentBalanceExceededException(
    val parentAvailable: Double,
    val attemptedTotal: Double
) : Exception(
    "La suma de los saldos hijo ($attemptedTotal) excede el disponible del padre ($parentAvailable)"
)

class ParentBalanceNotFoundException(val parentId: Long) : Exception(
    "No se encontró el saldo padre con id $parentId"
)