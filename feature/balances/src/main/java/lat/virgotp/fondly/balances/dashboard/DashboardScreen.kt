package lat.virgotp.fondly.balances.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import lat.virgotp.fondly.feature.balances.R
import kotlinx.coroutines.delay
import lat.virgotp.fondly.balances.dashboard.components.BalanceFilterBar
import lat.virgotp.fondly.ui_common.atoms.FondlyButton
import lat.virgotp.fondly.ui_common.atoms.FondlyButtonVariant
import lat.virgotp.fondly.ui_common.organisms.BalanceCard
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.Motion
import lat.virgotp.fondly.ui_common.theme.FondlySpacing
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.util.FondlyMoney

/** Sección [Saldos]: solo saldos base, FAB exclusivo de creación (RF-032/033). */
@Composable
fun DashboardScreen(
    onCreateBalanceClick: () -> Unit,
    onBalanceClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val currency = FondlyCurrencyController.rememberCurrency()

    FondlyScreenScaffold(
        title = stringResource(R.string.dashboard_title),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateBalanceClick,
                containerColor = scheme.primary,
                contentColor = scheme.onPrimary,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_create_balance)) }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = FondlySpacing.sm, bottom = 96.dp)
        ) {
            val total = uiState.balances.filter { it.isActive }.sumOf { it.available }

            item(key = "header") {
                Column(Modifier.padding(vertical = FondlySpacing.md)) {
                    Text(stringResource(R.string.dashboard_total_available), style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant)
                    Text(FondlyMoney.format(total, currency), style = MaterialTheme.typography.headlineLarge, color = scheme.primary)
                }
            }

            item(key = "filters") {
                BalanceFilterBar(
                    query = uiState.searchQuery,
                    statusFilter = uiState.statusFilter,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onStatusFilterChange = viewModel::onStatusFilterChange
                )
            }

            when {
                uiState.isLoading -> item(key = "loading") {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = scheme.primary)
                    }
                }
                uiState.balances.isEmpty() -> item(key = "empty") {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        EmptySaldosState(onCreateBalanceClick)
                    }
                }
                else -> itemsIndexed(uiState.balances, key = { _, b -> b.id }) { index, balance ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * Motion.STAGGER_MS)
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(Motion.DURATION_MEDIUM)) +
                                slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(Motion.DURATION_LONG)),
                        modifier = Modifier.animateItem()
                    ) {
                        BalanceCard(
                            balance = balance,
                            depth = 0,
                            parentName = null,
                            onClick = { onBalanceClick(balance.id) },
                            onEditClick = { onEditClick(balance.id) },
                            children = uiState.childrenByParent[balance.id].orEmpty(),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySaldosState(onCreateBalanceClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Box(
            Modifier.size(72.dp).clip(CircleShape).background(scheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
                Text(
                    FondlyCurrencyController.rememberCurrency().symbol,
                    style = MaterialTheme.typography.displaySmall,
                    color = scheme.primary
                )
            }
        Text(stringResource(R.string.dashboard_empty_title), style = MaterialTheme.typography.titleLarge, color = scheme.onSurface, modifier = Modifier.padding(top = FondlySpacing.md))
        Text(
            stringResource(R.string.dashboard_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.padding(top = FondlySpacing.sm)
        )
        FondlyButton(
            text = stringResource(R.string.action_create_balance), onClick = onCreateBalanceClick, variant = FondlyButtonVariant.Secondary,
            modifier = Modifier.padding(top = FondlySpacing.lg).fillMaxWidth(0.7f)
        )
    }
}