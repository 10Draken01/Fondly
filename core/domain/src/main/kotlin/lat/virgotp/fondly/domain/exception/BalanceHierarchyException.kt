package lat.virgotp.fondly.domain.exception

import java.math.BigDecimal

class ParentBalanceExceededException(
    val parentAvailable: BigDecimal,
    val attemptedTotal: BigDecimal
) : Exception(
    "La suma de los saldos hijo ($attemptedTotal) excede el disponible del padre ($parentAvailable)"
)

class ParentBalanceNotFoundException(val parentId: Long) : Exception(
    "No se encontró el saldo padre con id $parentId"
)

class BalanceCycleException(val balanceId: Long, val newParentId: Long) : Exception(
    "Asignar $newParentId como padre de $balanceId crearía un ciclo en la jerarquía"
)

class BalanceHierarchyDepthExceededException(val maxDepth: Int) : Exception(
    "Se excedió la profundidad máxima de jerarquía ($maxDepth); posible ciclo en los datos"
)
