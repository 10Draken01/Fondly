package lat.virgotp.fondly.balances.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCardOff
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import lat.virgotp.fondly.balances.detail.components.BalanceHeaderSection
import lat.virgotp.fondly.balances.detail.components.BalanceStatsSection
import lat.virgotp.fondly.ui_common.atoms.ConfirmationDialog
import lat.virgotp.fondly.ui_common.atoms.FondlyButton
import lat.virgotp.fondly.ui_common.atoms.FondlyButtonVariant
import lat.virgotp.fondly.ui_common.molecules.BalanceFieldLabels
import lat.virgotp.fondly.ui_common.molecules.LedgerRowData
import lat.virgotp.fondly.ui_common.molecules.LedgerSectionCard
import lat.virgotp.fondly.ui_common.organisms.BalanceCard
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.*

@Composable
fun BalanceDetailScreen(
    balanceId: Long,
    onEditClick: () -> Unit,
    onAddChildClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: BalanceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme

    var showToggleConfirm by remember { mutableStateOf(false) }
    var showRenewConfirm by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    FondlyScreenScaffold(
        title = "Detalle del saldo",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
    ) { paddingValues ->
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
                val maxTarget = balance.targetAmount
                val lockedDirect = uiState.children.filter { it.isActive }.sumOf { it.targetAmount }

                LazyColumn(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {
                    item(key = "header") {
                        BalanceHeaderSection(
                            name = balance.name,
                            hierarchyLabel = BalanceHierarchyColors.labelForDepth(depth) +
                                    (if (balance.isActive) "" else " · Inactivo"),
                            icon = BalanceHierarchyIcons.forDepth(depth),
                            hierarchyColor = if (balance.isActive) hierarchyColor else scheme.onSurfaceVariant,
                            available = balance.available
                        )
                    }

                    item(key = "stats") {
                        BalanceStatsSection(
                            balance = balance,
                            maxTarget = maxTarget,
                            total = balance.available,
                            lockedDirect = lockedDirect,
                            hierarchyColor = hierarchyColor,
                            descendantsByParentId = uiState.descendantsByParentId,
                            modifier = Modifier.padding(top = FondlySpacing.lg)
                        )
                    }

                    item(key = "actions") {
                        Column(Modifier.padding(top = FondlySpacing.xl)) {
                            Row {
                                FondlyButton(
                                    text = "Editar", onClick = onEditClick,
                                    icon = Icons.Filled.Edit,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(FondlySpacing.sm))
                                FondlyButton(
                                    text = "Renovar",
                                    onClick = { showRenewConfirm = true },
                                    icon = Icons.Filled.Autorenew,
                                    variant = FondlyButtonVariant.UtilSuccess,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(FondlySpacing.sm))
                            Row {
                                FondlyButton(
                                    text = if (balance.isActive) "Desactivar" else "Activar",
                                    onClick = { showToggleConfirm = true },
                                    icon = if (balance.isActive) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                                    variant = if (balance.isActive) FondlyButtonVariant.UtilTriggerOff else FondlyButtonVariant.UtilTriggerOn,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(FondlySpacing.sm))
                                FondlyButton(
                                    text = "Agregar apartado",
                                    onClick = onAddChildClick,
                                    icon = Icons.Filled.Add,
                                    variant = FondlyButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item(key = "info-general") {
                        LedgerSectionCard(
                            title = "Información general",
                            accentColor = hierarchyColor,
                            initiallyExpanded = false,
                            rows = listOf(
                                LedgerRowData("Descripción", balance.description ?: "—", Icons.Filled.Description)
                            ),
                            modifier = Modifier.padding(top = FondlySpacing.xl)
                        )
                    }

                    item(key = "info-config") {
                        LedgerSectionCard(
                            title = "Configuración",
                            accentColor = hierarchyColor,
                            initiallyExpanded = false,
                            rows = listOf(
                                LedgerRowData("Tipo", BalanceFieldLabels.typeLabel(balance.type), Icons.Filled.Tune),
                                LedgerRowData("Periodicidad", BalanceFieldLabels.periodicityLabel(balance.periodicity), Icons.Filled.Repeat),
                                LedgerRowData("Al renovar", BalanceFieldLabels.rolloverLabel(balance.rolloverStrategy), Icons.Filled.Autorenew),
                                LedgerRowData("Reajuste", BalanceFieldLabels.rebalanceLabel(balance.rebalanceStrategy), Icons.Filled.SwapVert),
                                LedgerRowData("Sobregiro", if (balance.allowOverdraft) "Permitido" else "No permitido", Icons.Filled.CreditCardOff),
                                LedgerRowData("Umbral de notificación", balance.notificationThreshold?.let { "$it%" } ?: "—", Icons.Filled.NotificationsActive)
                            ),
                            modifier = Modifier.padding(top = FondlySpacing.md)
                        )
                    }

                    item(key = "info-dates") {
                        LedgerSectionCard(
                            title = "Fechas y estado",
                            accentColor = hierarchyColor,
                            initiallyExpanded = false,
                            rows = listOf(
                                LedgerRowData("Próxima renovación", balance.renewalDate?.toString() ?: "—", Icons.Filled.Event),
                                LedgerRowData("Creado", balance.createdAt.toString().take(10), Icons.Filled.Schedule),
                                LedgerRowData("Estado", if (balance.isActive) "Activo" else "Inactivo", Icons.Filled.CheckCircle)
                            ),
                            modifier = Modifier.padding(top = FondlySpacing.md)
                        )
                    }

                    if (uiState.children.isNotEmpty()) {
                        item(key = "children-title") {
                            Text(
                                "Apartados (${uiState.children.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = scheme.onSurface,
                                modifier = Modifier.padding(top = FondlySpacing.xl)
                            )
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

                ConfirmationDialog(
                    show = showToggleConfirm,
                    title = if (balance.isActive) "Desactivar saldo" else "Activar saldo",
                    message = if (balance.isActive)
                        "¿Seguro que quieres desactivar \"${balance.name}\"? Podrás reactivarlo cuando quieras."
                    else
                        "¿Deseas volver a activar \"${balance.name}\"?",
                    confirmLabel = if (balance.isActive) "Desactivar" else "Activar",
                    confirmColor = if (balance.isActive) scheme.onSurfaceVariant else scheme.primary,
                    icon = if (balance.isActive) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                    onConfirm = {
                        viewModel.onToggleActive()
                        showToggleConfirm = false
                    },
                    onDismiss = { showToggleConfirm = false }
                )

                ConfirmationDialog(
                    show = showRenewConfirm,
                    title = "Renovar saldo",
                    message = "Esto renovará el ciclo de \"${balance.name}\" según su periodicidad. (Función en desarrollo)",
                    confirmLabel = "Renovar",
                    confirmColor = SuccessGreen,
                    icon = Icons.Filled.Autorenew,
                    onConfirm = {
                        viewModel.onClickResetBalance()
                        showRenewConfirm = false
                    },
                    onDismiss = { showRenewConfirm = false }
                )
            }
        }
    }
}