package lat.virgotp.fondly.balances.detail.components

import java.math.RoundingMode
import java.math.BigDecimal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.ui_common.molecules.BalanceProgressSection
import lat.virgotp.fondly.ui_common.molecules.LegendItem
import lat.virgotp.fondly.ui_common.molecules.SegmentedStatRow
import lat.virgotp.fondly.ui_common.theme.BalanceHierarchyIcons
import lat.virgotp.fondly.ui_common.theme.BalanceSegmentColors
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.feature.balances.R
import lat.virgotp.fondly.uicommon.R as UiCommonR
import lat.virgotp.fondly.ui_common.util.buildBalanceCardSegments

/**
 * Barras del detalle:
 *  - Total y Libre: filas de 2 segmentos [valor][vacio], relativas al
 *    target MAXIMO del balance raiz (maxTarget).
 *  - Cada apartado/nieto/bisnieto...: una mini replica EXACTA de la
 *    barra de BalanceCard (Libre + descendientes + Usado), pero
 *    calculada con el target y available PROPIOS de ese nodo — igual
 *    que si esa card se mostrara sola.
 */
@Composable
fun BalanceStatsSection(
    balance: Balance,
    maxTarget: BigDecimal,
    total: BigDecimal,
    lockedDirect: BigDecimal,
    hierarchyColor: Color,
    descendantsByParentId: Map<Long, List<Balance>>,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val free = total - lockedDirect
    val totalFraction = if (maxTarget.signum() > 0) total.divide(maxTarget, 6, RoundingMode.HALF_EVEN).toFloat().coerceIn(0f, 1f) else 0f
    val freeFraction = if (maxTarget.signum() > 0) free.divide(maxTarget, 6, RoundingMode.HALF_EVEN).toFloat().coerceIn(0f, 1f) else 0f
    val emptyColor = scheme.surfaceVariant

    val descendants = remember(balance.id, descendantsByParentId) {
        flattenDescendants(balance.id, level = 1, childrenByParentId = descendantsByParentId)
    }

    Column(modifier.fillMaxWidth()) {
        SegmentedStatRow(
            label = stringResource(R.string.detail_stat_total), amount = total, fraction = totalFraction,
            color = hierarchyColor, emptyColor = emptyColor, icon = Icons.Filled.AccountBalanceWallet
        )
        Spacer(Modifier.height(FondlySpacing.lg))
        SegmentedStatRow(
            label = stringResource(R.string.detail_stat_free), amount = free, fraction = freeFraction,
            color = scheme.primary, emptyColor = emptyColor, icon = Icons.Filled.LockOpen
        )

        if (descendants.isNotEmpty()) {
            Spacer(Modifier.height(FondlySpacing.xl))
            Text(stringResource(R.string.detail_sections_title), style = MaterialTheme.typography.titleSmall, color = scheme.onSurfaceVariant)
            Spacer(Modifier.height(FondlySpacing.sm))
            descendants.forEach { (level, node) ->
                DescendantStatBlock(
                    level = level,
                    node = node,
                    childrenByParentId = descendantsByParentId,
                    modifier = Modifier.padding(start = ((level - 1) * 16).dp, top = FondlySpacing.md)
                )
            }
        }
    }
}

/** Recorre TODO el subarbol (hijos, nietos, bisnietos...) en orden, con su nivel absoluto. */
private fun flattenDescendants(
    parentId: Long,
    level: Int,
    childrenByParentId: Map<Long, List<Balance>>
): List<Pair<Int, Balance>> {
    val kids = childrenByParentId[parentId].orEmpty().filter { it.isActive }
    return kids.flatMap { child ->
        listOf(level to child) + flattenDescendants(child.id, level + 1, childrenByParentId)
    }
}

/** Fila de un apartado/nieto: encabezado con su nombre + su propia mini-barra estilo card. */
@Composable
private fun DescendantStatBlock(
    level: Int,
    node: Balance,
    childrenByParentId: Map<Long, List<Balance>>,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val accentColor = BalanceSegmentColors.forLevel(level)
    val target = node.targetAmount
    val freeFraction = if (target.signum() > 0) node.available.divide(target, 6, RoundingMode.HALF_EVEN).toFloat().coerceIn(0f, 1f) else 0f

    // Colores resueltos aqui (contexto @Composable) antes de pasarlos
    // como lambda comun a buildBalanceCardSegments (funcion no-composable).
    val levelColors = (1..8).map { BalanceSegmentColors.forLevel(it) }
    val colorForLevel: (Int) -> Color = { lvl -> levelColors.getOrElse(lvl - 1) { levelColors.last() } }

    val segments = buildBalanceCardSegments(
        nodeId = node.id,
        target = target,
        available = node.available,
        childrenByParentId = childrenByParentId,
        freeColor = scheme.primary,
        usedColor = scheme.outline,
        freeLabel = stringResource(UiCommonR.string.segment_free),
        usedLabel = stringResource(UiCommonR.string.segment_used),
        colorForLevel = colorForLevel
    )
    val legendItems = segments.map { LegendItem(it.label, it.color) }

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                BalanceHierarchyIcons.forDepth(level),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(FondlySpacing.xs))
            Text(node.name, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
        }
        Spacer(Modifier.height(FondlySpacing.xs))
        BalanceProgressSection(
            segments = segments,
            legendItems = legendItems,
            available = node.available,
            target = target,
            freeFraction = freeFraction,
            freeColor = scheme.primary
        )
    }
}