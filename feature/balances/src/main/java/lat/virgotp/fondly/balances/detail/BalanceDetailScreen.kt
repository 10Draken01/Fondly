package lat.virgotp.fondly.balances.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import lat.virgotp.fondly.feature.balances.R
import lat.virgotp.fondly.balances.components.BalanceFieldLabels
import lat.virgotp.fondly.balances.detail.components.BalanceHeaderSection
import lat.virgotp.fondly.balances.detail.components.BalanceStatsSection
import lat.virgotp.fondly.ui_common.atoms.ConfirmationDialog
import lat.virgotp.fondly.ui_common.atoms.FondlyButton
import lat.virgotp.fondly.ui_common.atoms.FondlyButtonVariant
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
        title = stringResource(R.string.detail_title),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
        }
    ) { paddingValues ->
        val balance = uiState.balance

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = scheme.primary)
            }
            balance == null -> Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.detail_balance_not_found), style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
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
                                    (if (balance.isActive) "" else " · " + stringResource(R.string.detail_inactive_suffix)),
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
                                    text = stringResource(R.string.action_edit), onClick = onEditClick,
                                    icon = Icons.Filled.Edit,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(FondlySpacing.sm))
                                FondlyButton(
                                    text = stringResource(R.string.action_renew),
                                    onClick = { showRenewConfirm = true },
                                    icon = Icons.Filled.Autorenew,
                                    variant = FondlyButtonVariant.UtilSuccess,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(FondlySpacing.sm))
                            Row {
                                FondlyButton(
                                    text = stringResource(if (balance.isActive) R.string.action_deactivate else R.string.action_activate),
                                    onClick = { showToggleConfirm = true },
                                    icon = if (balance.isActive) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                                    variant = if (balance.isActive) FondlyButtonVariant.UtilTriggerOff else FondlyButtonVariant.UtilTriggerOn,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(FondlySpacing.sm))
                                FondlyButton(
                                    text = stringResource(R.string.action_add_section),
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
                            title = stringResource(R.string.detail_info_general),
                            accentColor = hierarchyColor,
                            initiallyExpanded = false,
                            rows = listOf(
                                LedgerRowData(stringResource(R.string.detail_description), balance.description ?: "—", Icons.Filled.Description)
                            ),
                            modifier = Modifier.padding(top = FondlySpacing.xl)
                        )
                    }

                    item(key = "info-config") {
                        LedgerSectionCard(
                            title = stringResource(R.string.detail_config),
                            accentColor = hierarchyColor,
                            initiallyExpanded = false,
                            rows = listOf(
                                LedgerRowData(stringResource(R.string.detail_type), BalanceFieldLabels.typeLabel(balance.type), Icons.Filled.Tune),
                                LedgerRowData(stringResource(R.string.detail_periodicity), BalanceFieldLabels.periodicityLabel(balance.periodicity), Icons.Filled.Repeat),
                                LedgerRowData(stringResource(R.string.detail_rollover), BalanceFieldLabels.rolloverLabel(balance.rolloverStrategy), Icons.Filled.Autorenew),
                                LedgerRowData(stringResource(R.string.detail_rebalance), BalanceFieldLabels.rebalanceLabel(balance.rebalanceStrategy), Icons.Filled.SwapVert),
                                LedgerRowData(stringResource(R.string.detail_overdraft), stringResource(if (balance.allowOverdraft) R.string.detail_overdraft_allowed else R.string.detail_overdraft_denied), Icons.Filled.CreditCardOff),
                                LedgerRowData(stringResource(R.string.detail_threshold), balance.notificationThreshold?.let { "$it%" } ?: "—", Icons.Filled.NotificationsActive)
                            ),
                            modifier = Modifier.padding(top = FondlySpacing.md)
                        )
                    }

                    item(key = "info-dates") {
                        LedgerSectionCard(
                            title = stringResource(R.string.detail_dates),
                            accentColor = hierarchyColor,
                            initiallyExpanded = false,
                            rows = listOf(
                                LedgerRowData(stringResource(R.string.detail_next_renewal), balance.renewalDate?.toString() ?: "—", Icons.Filled.Event),
                                LedgerRowData(stringResource(R.string.detail_created), balance.createdAt.toString().take(10), Icons.Filled.Schedule),
                                LedgerRowData(stringResource(R.string.detail_status), stringResource(if (balance.isActive) R.string.detail_status_active else R.string.detail_status_inactive), Icons.Filled.CheckCircle)
                            ),
                            modifier = Modifier.padding(top = FondlySpacing.md)
                        )
                    }

                    if (uiState.children.isNotEmpty()) {
                        item(key = "children-title") {
                            Text(
                                stringResource(R.string.detail_sections_title) + " (${uiState.children.size})",
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
                    title = stringResource(if (balance.isActive) R.string.detail_confirm_deactivate_title else R.string.detail_confirm_activate_title),
                    message = stringResource(
                        if (balance.isActive) R.string.detail_confirm_deactivate_message
                        else R.string.detail_confirm_activate_message,
                        balance.name
                    ),
                    confirmLabel = stringResource(if (balance.isActive) R.string.action_deactivate else R.string.action_activate),
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
                    title = stringResource(R.string.detail_confirm_renew_title),
                    message = stringResource(R.string.detail_confirm_renew_message, balance.name),
                    confirmLabel = stringResource(R.string.action_renew),
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