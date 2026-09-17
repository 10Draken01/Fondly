package lat.virgotp.fondly.balances.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlin.math.roundToInt
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.ui_common.atoms.*
import lat.virgotp.fondly.ui_common.molecules.BalanceFieldLabels
import lat.virgotp.fondly.ui_common.organisms.BalanceCard
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.*
import lat.virgotp.fondly.ui_common.util.FondlyMoney

@Composable
fun BalanceDetailScreen(
    balanceId: Long,
    onEditClick: () -> Unit,
    onAddChildClick: () -> Unit,
    viewModel: BalanceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme

    FondlyScreenScaffold(title = "Detalle del saldo") { paddingValues ->
        val balance = uiState.balance

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = scheme.primary)
            }
            balance == null -> Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No se encontró el saldo.", style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
            }
            else -> {
                val depth = if (balance.parentBalanceId == null) 0 else 1
                val hierarchyColor = BalanceHierarchyColors.forDepth(depth)
                val target = balance.targetAmount
                val locked = uiState.children.filter { it.isActive }.sumOf { it.targetAmount }
                val free = (target - locked).coerceAtLeast(0.0)
                val pct: (Double) -> Int = { part ->
                    if (target > 0) ((part / target) * 100).roundToInt() else 0
                }

                LazyColumn(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {
                    item(key = "hero") {
                        GlassSurface(glowColor = hierarchyColor) {
                            Column(Modifier.padding(FondlySpacing.lg)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(44.dp).clip(CircleShape)
                                            .background(hierarchyColor.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(balance.name.firstOrNull()?.uppercase() ?: "$",
                                            style = MaterialTheme.typography.titleMedium, color = hierarchyColor)
                                    }
                                    Column(Modifier.padding(start = FondlySpacing.md)) {
                                        Text(balance.name, style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface)
                                        Text(
                                            BalanceHierarchyColors.labelForDepth(depth) +
                                                    (if (balance.isActive) "" else " · Inactivo"),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (balance.isActive) hierarchyColor else scheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    FondlyMoney.formatMXN(balance.available),
                                    style = MaterialTheme.typography.displaySmall, color = scheme.primary,
                                    modifier = Modifier.padding(top = FondlySpacing.sm)
                                )

                                Spacer(Modifier.height(FondlySpacing.md))

                                // RF-038: tres barras independientes con nombre, monto y %
                                StatBar("Total", target, 1f, scheme.primary)
                                StatBar("Apartados", locked, if (target > 0) (locked / target).toFloat() else 0f, scheme.onSurfaceVariant)
                                StatBar("Libre", free, if (target > 0) (free / target).toFloat() else 0f, hierarchyColor)
                            }
                        }
                    }

                    item(key = "meta") {
                        GlassSurface(glowColor = hierarchyColor, modifier = Modifier.padding(top = FondlySpacing.md)) {
                            Column(Modifier.padding(FondlySpacing.lg), verticalArrangement = Arrangement.spacedBy(FondlySpacing.xs)) {
                                Text("Información", style = MaterialTheme.typography.titleMedium, color = scheme.onSurface)
                                MetaRow("Descripción", balance.description ?: "—")
                                MetaRow("Tipo", BalanceFieldLabels.typeLabel(balance.type))
                                MetaRow("Periodicidad", BalanceFieldLabels.periodicityLabel(balance.periodicity))
                                MetaRow("Próxima renovación", balance.renewalDate?.toString() ?: "—")
                                MetaRow("Al renovar", BalanceFieldLabels.rolloverLabel(balance.rolloverStrategy))
                                MetaRow("Reajuste", BalanceFieldLabels.rebalanceLabel(balance.rebalanceStrategy))
                                MetaRow("Sobregiro", if (balance.allowOverdraft) "Permitido" else "No permitido")
                                MetaRow("Umbral de notificación", balance.notificationThreshold?.let { "$it%" } ?: "—")
                                MetaRow("Estado", if (balance.isActive) "Activo" else "Inactivo")
                                MetaRow("Creado", balance.createdAt.toString().take(10))
                            }
                        }
                    }

                    item(key = "actions") {
                        Column(Modifier.padding(top = FondlySpacing.lg)) {
                            Row {
                                FondlyButton("Editar", onEditClick, Modifier.weight(1f))
                                Spacer(Modifier.width(FondlySpacing.sm))
                                FondlyButton(
                                    text = if (balance.isActive) "Desactivar" else "Activar",
                                    onClick = viewModel::onToggleActive,
                                    variant = if (balance.isActive) FondlyButtonVariant.Secondary else FondlyButtonVariant.Primary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(FondlySpacing.sm))
                            FondlyButton("Agregar apartado", onAddChildClick, variant = FondlyButtonVariant.Secondary)
                        }
                    }

                    if (uiState.children.isNotEmpty()) {
                        item(key = "children-title") {
                            Text("Apartados (${uiState.children.size})", style = MaterialTheme.typography.titleMedium,
                                color = scheme.onSurface, modifier = Modifier.padding(top = FondlySpacing.xl))
                        }
                        items(uiState.children, key = { it.id }) { child ->
                            BalanceCard(
                                balance = child, depth = 1, parentName = balance.name,
                                onClick = { }, onEditClick = onEditClick,
                                modifier = Modifier.padding(top = FondlySpacing.sm)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBar(label: String, amount: Double, fraction: Float, color: androidx.compose.ui.graphics.Color) {
    val scheme = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(vertical = FondlySpacing.xxs)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                Spacer(Modifier.width(FondlySpacing.xs))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
            }
            Text(
                "${FondlyMoney.formatMXN(amount)} · ${(fraction * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelLarge, color = scheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        FondlyProgressBar(fraction = fraction, progressColor = color)
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    val scheme = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
    }
}