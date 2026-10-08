package lat.virgotp.fondly.application.usecase

import lat.virgotp.fondly.domain.exception.BalanceHierarchyDepthExceededException
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.port.BalanceRepository

/**
 * Guardia defensiva compartida para recorridos de jerarquía (descendientes
 * o subárboles). Protege contra ciclos accidentales en los datos incluso si
 * la validación de escritura falló o los datos vienen corruptos (BR-001).
 *
 * - Conjunto de IDs visitados: un id repetido detiene la rama (ciclo).
 * - [MAX_HIERARCHY_DEPTH]: límite razonable de profundidad; excederlo es
 *   un error de datos, nunca un loop infinito.
 */
const val MAX_HIERARCHY_DEPTH = 50

/**
 * Recorre los descendientes de [rootId] en DFS con protección de ciclos.
 * Devuelve todos los descendientes únicos (excluye al propio [rootId]).
 */
internal suspend fun BalanceRepository.collectDescendants(rootId: Long): List<Balance> {
    val visited = mutableSetOf<Long>()
    val result = mutableListOf<Balance>()

    suspend fun recurse(parentId: Long, depth: Int) {
        if (depth > MAX_HIERARCHY_DEPTH) {
            throw BalanceHierarchyDepthExceededException(MAX_HIERARCHY_DEPTH)
        }
        for (child in getChildrenOf(parentId)) {
            if (!visited.add(child.id)) continue // ciclo: rama ya visitada
            result += child
            recurse(child.id, depth + 1)
        }
    }
    recurse(rootId, depth = 1)
    return result
}
