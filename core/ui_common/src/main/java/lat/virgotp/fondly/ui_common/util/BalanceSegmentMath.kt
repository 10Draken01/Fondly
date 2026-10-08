package lat.virgotp.fondly.ui_common.util

import java.math.BigDecimal
import java.math.RoundingMode
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.ui_common.atoms.BarSegment
import androidx.compose.ui.graphics.Color

/** Un tramo de la jerarquia de apartados, listo para pintarse como segmento de barra. */
data class LeveledFraction(
    val level: Int,
    val fraction: Float,
    val amount: BigDecimal,
    val label: String
)

/** División monetaria segura para derivar fracciones de pantalla (solo visual). */
private fun BigDecimal.divideToFloat(other: BigDecimal): Float =
    if (other.signum() == 0) 0f
    else divide(other, 6, RoundingMode.HALF_EVEN).toFloat()

/**
 * Recorre el arbol de apartados (hijos, nietos, bisnietos... nivel infinito)
 * por NIVELES (BFS) y calcula, para cada nodo activo, la fraccion de
 * [rootTargetAmount] que representa SOLO la parte de su propio target
 * que no esta a su vez reservada por sus propios hijos activos.
 *
 * Esto evita contar el mismo dinero dos veces: si un apartado "Vacaciones"
 * tiene $1000 de target pero $400 estan reservados en su hijo "Vuelos",
 * "Vacaciones" solo aporta $600 como SU propio segmento; los $400 los
 * aporta "Vuelos" (o los hijos de "Vuelos", recursivamente).
 *
 * La suma total de las fracciones devueltas equivale exactamente a
 * (targetAmount de los hijos DIRECTOS del root) / rootTargetAmount,
 * por telescopia — asi el calculo de "usado" en el organismo no necesita
 * recorrer el arbol de nuevo.
 */
fun buildDescendantFractions(
    rootId: Long,
    rootTargetAmount: BigDecimal,
    childrenByParentId: Map<Long, List<Balance>>
): List<LeveledFraction> {
    if (rootTargetAmount.signum() <= 0) return emptyList()

    val result = mutableListOf<LeveledFraction>()
    var currentLevel = childrenByParentId[rootId].orEmpty().filter { it.isActive }
    var level = 1

    while (currentLevel.isNotEmpty()) {
        val nextLevel = mutableListOf<Balance>()
        for (node in currentLevel) {
            val activeChildren = childrenByParentId[node.id].orEmpty().filter { it.isActive }
            val childrenSum = activeChildren.fold(BigDecimal.ZERO) { acc, b -> acc + b.targetAmount }
            val ownAmount = (node.targetAmount - childrenSum).coerceAtLeast(BigDecimal.ZERO)
            val ownFraction = ownAmount.divideToFloat(rootTargetAmount).coerceIn(0f, 1f)
            if (ownFraction > 0f) {
                result += LeveledFraction(level, ownFraction, ownAmount, node.name)
            }
            nextLevel += activeChildren
        }
        currentLevel = nextLevel
        level++
    }
    return result
}

/**
 * Algoritmo tipo "flexbox" para repartir anchos en pixeles respetando
 * un minimo por segmento.
 *
 * Paso 1: asigna a cada segmento su ancho proporcional ideal.
 * Paso 2: cualquier segmento que quede por debajo de [minWidthPx] se
 *         "fija" en el minimo y sale del reparto.
 * Paso 3: el ancho restante se reparte proporcionalmente SOLO entre los
 *         segmentos aun no fijados.
 * Se repite hasta que ninguno quede por debajo del minimo (convergencia
 * garantizada porque en cada iteracion al menos un segmento se fija).
 */
fun distributeSegmentWidths(
    fractions: List<Float>,
    totalWidthPx: Float,
    minWidthPx: Float
): List<Float> {
    val n = fractions.size
    if (n == 0 || totalWidthPx <= 0f) return List(n) { 0f }

    // Caso patologico: ni siquiera el minimo cabe para todos -> reparto igualitario.
    if (minWidthPx * n >= totalWidthPx) {
        val equalShare = totalWidthPx / n
        return List(n) { equalShare }
    }

    val fixed = BooleanArray(n)
    val widths = FloatArray(n)
    var remainingWidth = totalWidthPx
    var remainingFractionSum = fractions.sum().coerceAtLeast(0.0001f)

    var changed = true
    while (changed) {
        changed = false
        for (i in 0 until n) {
            if (fixed[i]) continue
            val proposed = (fractions[i] / remainingFractionSum) * remainingWidth
            if (proposed < minWidthPx) {
                widths[i] = minWidthPx
                fixed[i] = true
                remainingWidth -= minWidthPx
                remainingFractionSum -= fractions[i]
                changed = true
            }
        }
    }
    for (i in 0 until n) {
        if (!fixed[i]) {
            widths[i] = (fractions[i] / remainingFractionSum) * remainingWidth
        }
    }
    return widths.toList()
}

/**
 * Arma los segmentos de una barra "estilo BalanceCard" para CUALQUIER
 * balance (sea el saldo raiz o un apartado/nieto individual): Libre,
 * luego sus descendientes recursivos (hijos, nietos, ...), luego Usado.
 * Todo relativo al PROPIO target del balance (no al de un ancestro),
 * exactamente como se ve una BalanceCard si se renderizara sola.
 *
 * Compartida entre BalanceCard (organismo) y las filas de apartados del
 * detalle, para que ambas vistas nunca diverjan en la logica.
 */
fun buildBalanceCardSegments(
    nodeId: Long,
    target: BigDecimal,
    available: BigDecimal,
    childrenByParentId: Map<Long, List<Balance>>,
    freeColor: Color,
    usedColor: Color,
    freeLabel: String,
    usedLabel: String,
    colorForLevel: (Int) -> Color
): List<BarSegment> {
    if (target.signum() <= 0) return emptyList()

    val freeFraction = available.divideToFloat(target).coerceIn(0f, 1f)
    val descendantFractions = buildDescendantFractions(nodeId, target, childrenByParentId)
    val reservedFraction = descendantFractions.sumOf { it.fraction.toDouble() }.toFloat().coerceIn(0f, 1f)
    val usedFraction = (1f - freeFraction - reservedFraction).coerceIn(0f, 1f)

    return buildList {
        if (freeFraction > 0f) add(BarSegment(freeFraction, freeColor, freeLabel))
        descendantFractions.forEach { leveled ->
            add(BarSegment(leveled.fraction, colorForLevel(leveled.level), leveled.label))
        }
        if (usedFraction > 0f) add(BarSegment(usedFraction, usedColor, usedLabel))
    }
}