package lat.virgotp.fondly.ui_common.organisms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.ui_common.atoms.CornerRibbon
import lat.virgotp.fondly.ui_common.atoms.FondlyProgressBar
import lat.virgotp.fondly.ui_common.atoms.FondlySegmentedProgressBar
import lat.virgotp.fondly.ui_common.atoms.GlassSurface
import lat.virgotp.fondly.ui_common.theme.BalanceHierarchyColors
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.Motion
import lat.virgotp.fondly.ui_common.util.FondlyMoney

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

    val activeChildren = children.filter { it.isActive }
    val lockedByChildren = activeChildren.sumOf { it.targetAmount }
    val free = (target - lockedByChildren).coerceAtLeast(0.0)
    val freeFraction = if (target > 0) (free / target).toFloat().coerceIn(0f, 1f) else 0f
    val consumedFraction = if (target > 0) (balance.available / target).toFloat().coerceIn(0f, 1f) else 0f

    val grayBase = scheme.onSurfaceVariant
    val grays = listOf(0.50f, 0.65f, 0.80f, 0.95f).map { grayBase.copy(alpha = it) }

    val segments: List<Pair<Float, Color>> =
        if (activeChildren.isNotEmpty() && target > 0) {
            activeChildren.mapIndexed { index, child ->
                (child.targetAmount / target).toFloat().coerceIn(0f, 1f) to grays[index % grays.size]
            }
        } else emptyList()

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(hierarchyColor.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            balance.name.firstOrNull()?.uppercase() ?: "$",
                            style = MaterialTheme.typography.titleMedium,
                            color = hierarchyColor
                        )
                    }
                    Column(
                        Modifier.padding(start = FondlySpacing.md).weight(1f)
                    ) {
                        Text(
                            balance.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = scheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            parentName?.let { "${BalanceHierarchyColors.labelForDepth(depth)} · $it" }
                                ?: BalanceHierarchyColors.labelForDepth(depth),
                            style = MaterialTheme.typography.bodySmall,
                            color = hierarchyColor
                        )
                    }
                    if (onEditClick != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar saldo",
                                tint = hierarchyColor
                            )
                        }
                    }
                }

                Spacer(Modifier.height(FondlySpacing.md))

                if (segments.isNotEmpty()) {
                    FondlySegmentedProgressBar(segments = segments)
                } else {
                    FondlyProgressBar(fraction = consumedFraction, progressColor = hierarchyColor)
                }

                Row(
                    Modifier.fillMaxWidth().padding(top = FondlySpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Total: ${FondlyMoney.formatMXN(target)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                        if (segments.isNotEmpty()) {
                            Text(
                                "Libre: ${FondlyMoney.formatMXN(free)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = scheme.primary
                            )
                        } else {
                            Text(
                                "${FondlyMoney.formatMXN(balance.available)} disponibles",
                                style = MaterialTheme.typography.labelLarge,
                                color = scheme.primary
                            )
                        }
                    }
                    if (segments.isNotEmpty()) {
                        Text(
                            "${(freeFraction * 100).roundToInt()}% libre",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "${(consumedFraction * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = hierarchyColor
                        )
                    }
                }
            }
        }
        CornerRibbon(
            level = depth + 1,
            color = hierarchyColor,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}