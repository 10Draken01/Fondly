package lat.virgotp.fondly.ui_common.organisms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.ui_common.atoms.BarSegment
import lat.virgotp.fondly.ui_common.util.buildBalanceCardSegments
import lat.virgotp.fondly.ui_common.atoms.CornerRibbon
import lat.virgotp.fondly.ui_common.atoms.GlassSurface
import lat.virgotp.fondly.ui_common.molecules.BalanceHeader
import lat.virgotp.fondly.ui_common.molecules.BalanceProgressSection
import lat.virgotp.fondly.ui_common.molecules.LegendItem
import lat.virgotp.fondly.ui_common.theme.BalanceHierarchyColors
import lat.virgotp.fondly.ui_common.theme.BalanceHierarchyIcons
import lat.virgotp.fondly.ui_common.theme.BalanceSegmentColors
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.Motion
import lat.virgotp.fondly.ui_common.util.buildDescendantFractions

@Composable
fun BalanceCard(
    balance: Balance,
    depth: Int,
    parentName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    children: List<Balance> = emptyList(),
    onEditClick: (() -> Unit)? = null
) {
    val scheme = MaterialTheme.colorScheme
    val hierarchyColor = BalanceHierarchyColors.forDepth(depth)
    val target = balance.targetAmount

    // available = saldo LIBRE (no gastado ni reservado en apartados).
    val freeFraction = if (target > 0) (balance.available / target).toFloat().coerceIn(0f, 1f) else 0f
    val freeColor = scheme.primary   // dorado en ambos temas (Gold700 / E4BC55)
    val usedColor = scheme.outline   // "vacio/consumido": neutro, obviamente distinto del dorado

    val childrenByParentId = remember(children) {
        children.filter { it.parentBalanceId != null }.groupBy { it.parentBalanceId!! }
    }

    val descendantFractions = remember(balance.id, target, childrenByParentId) {
        buildDescendantFractions(
            rootId = balance.id,
            rootTargetAmount = target,
            childrenByParentId = childrenByParentId
        )
    }

    // Por telescopia, la suma de todos los niveles = target de los hijos DIRECTOS / target raiz.
    val reservedFraction = descendantFractions.sumOf { it.fraction.toDouble() }.toFloat().coerceIn(0f, 1f)
    val usedFraction = (1f - freeFraction - reservedFraction).coerceIn(0f, 1f)

    // Colores por nivel resueltos AQUI (en contexto @Composable) porque
    // BalanceSegmentColors.forLevel es @Composable y no puede llamarse
    // dentro de una lambda comun como la que recibe buildBalanceCardSegments.
    val levelColors = (1..8).map { BalanceSegmentColors.forLevel(it) }
    val colorForLevel: (Int) -> Color = { lvl -> levelColors.getOrElse(lvl - 1) { levelColors.last() } }

    val segments = buildBalanceCardSegments(
        nodeId = balance.id,
        target = target,
        available = balance.available,
        childrenByParentId = childrenByParentId,
        freeColor = scheme.primary,
        usedColor = scheme.outline,
        colorForLevel = colorForLevel
    )

    val legendItems = segments.map { LegendItem(it.label, it.color) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = tween(Motion.DURATION_SHORT),
        label = "cardScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(MaterialTheme.shapes.large)
            .alpha(if (balance.isActive) 1f else 0.55f)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        GlassSurface(cornerRadius = 24.dp, glowColor = hierarchyColor) {
            Column(modifier = Modifier.padding(FondlySpacing.lg)) {
                BalanceHeader(
                    name = balance.name,
                    hierarchyLabel = parentName?.let { "${BalanceHierarchyColors.labelForDepth(depth)} · $it" }
                        ?: BalanceHierarchyColors.labelForDepth(depth),
                    icon = BalanceHierarchyIcons.forDepth(depth),
                    hierarchyColor = hierarchyColor,
                    onEditClick = onEditClick
                )

                Spacer(Modifier.height(FondlySpacing.md))

                BalanceProgressSection(
                    segments = segments,
                    legendItems = legendItems,
                    available = balance.available,
                    target = target,
                    freeFraction = freeFraction,
                    freeColor = freeColor
                )
            }
        }
        CornerRibbon(
            level = depth + 1,
            color = hierarchyColor,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}