package lat.virgotp.fondly.balances.editbalance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.ui_common.atoms.FondlyButton
import lat.virgotp.fondly.ui_common.atoms.GlassSurface
import lat.virgotp.fondly.ui_common.molecules.*
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

@Composable
fun EditBalanceScreen(
    balanceId: Long,
    onSaved: () -> Unit,
    viewModel: EditBalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val labels = BalanceFieldLabels

    LaunchedEffect(uiState.savedSuccessfully) { if (uiState.savedSuccessfully) onSaved() }

    FondlyScreenScaffold(title = "Editar saldo") { paddingValues ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = scheme.primary)
            }
            else -> Column(
                Modifier.fillMaxSize().padding(paddingValues)
                    .verticalScroll(rememberScrollState()).padding(20.dp)
            ) {
                GlassSurface {
                    Column(Modifier.padding(FondlySpacing.lg)) {
                        FormField(uiState.name, viewModel::onNameChange, "Nombre",
                            capitalization = KeyboardCapitalization.Sentences)
                        FormField(uiState.targetAmount, viewModel::onTargetAmountChange, "Monto objetivo",
                            keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(top = FondlySpacing.md))
                        FormField(uiState.description, viewModel::onDescriptionChange, "Descripción (opcional)",
                            capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done,
                            modifier = Modifier.padding(top = FondlySpacing.md))

                        FondlyDropdownField("Tipo", labels.types.map(labels::typeLabel),
                            labels.types.indexOf(uiState.type), { viewModel.onTypeChange(labels.types[it]) },
                            Modifier.padding(top = FondlySpacing.md))

                        FondlyDropdownField("Periodicidad de renovación",
                            labels.periodicities.map(labels::periodicityLabel),
                            labels.periodicities.indexOf(uiState.periodicity),
                            { viewModel.onPeriodicityChange(labels.periodicities[it]) },
                            Modifier.padding(top = FondlySpacing.md))

                        val hasPeriodicity = uiState.periodicity != Periodicity.NONE

                        FondlyDateField("Fecha de renovación", uiState.renewalDate,
                            viewModel::onRenewalDateChange, enabled = hasPeriodicity,
                            modifier = Modifier.padding(top = FondlySpacing.md))

                        FondlyDropdownField("Al renovar (sobrante)", labels.rollovers.map(labels::rolloverLabel),
                            labels.rollovers.indexOf(uiState.rolloverStrategy),
                            { viewModel.onRolloverChange(labels.rollovers[it]) },
                            enabled = hasPeriodicity, modifier = Modifier.padding(top = FondlySpacing.md))

                        FondlyDropdownField("Estrategia de reajuste", labels.rebalances.map(labels::rebalanceLabel),
                            labels.rebalances.indexOf(uiState.rebalanceStrategy),
                            { viewModel.onRebalanceChange(labels.rebalances[it]) },
                            modifier = Modifier.padding(top = FondlySpacing.md))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = FondlySpacing.sm)) {
                            Checkbox(uiState.allowOverdraft, viewModel::onAllowOverdraftChange,
                                colors = CheckboxDefaults.colors(checkedColor = scheme.primary, checkmarkColor = scheme.onPrimary))
                            Text("Permitir sobregiro", style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = uiState.isActive,
                                onCheckedChange = viewModel::onIsActiveChange,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = scheme.primary,
                                    checkedThumbColor = scheme.onPrimary
                                )
                            )
                            Spacer(Modifier.width(FondlySpacing.sm))
                            Text(
                                if (uiState.isActive) "Saldo activo" else "Saldo inactivo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (uiState.isActive) scheme.primary else scheme.onSurfaceVariant
                            )
                        }

                        FormField(uiState.notificationThreshold, viewModel::onNotificationThresholdChange,
                            "Umbral de notificación % (opcional)", keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done, modifier = Modifier.padding(top = FondlySpacing.sm))

                        if (uiState.errorMessage != null) {
                            Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodySmall,
                                color = scheme.error, modifier = Modifier.padding(top = FondlySpacing.sm))
                        }
                    }
                }

                Spacer(Modifier.height(FondlySpacing.lg))
                FondlyButton(text = "Guardar cambios", onClick = viewModel::onSaveClick, isLoading = uiState.isSaving)
            }
        }
    }
}