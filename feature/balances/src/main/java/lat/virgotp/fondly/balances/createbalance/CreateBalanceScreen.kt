package lat.virgotp.fondly.balances.createbalance

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
import lat.virgotp.fondly.ui_common.atoms.FondlyButton
import lat.virgotp.fondly.ui_common.atoms.GlassSurface
import lat.virgotp.fondly.ui_common.molecules.*
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/** Formulario completo (RF-039): todos los campos del modelo. */
@Composable
fun CreateBalanceScreen(
    parentBalanceId: Long?,
    onSaved: () -> Unit,
    viewModel: CreateBalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val labels = BalanceFieldLabels

    LaunchedEffect(uiState.savedSuccessfully) { if (uiState.savedSuccessfully) onSaved() }

    FondlyScreenScaffold(title = if (parentBalanceId != null) "Nuevo apartado" else "Crear saldo") { paddingValues ->
        Column(
            Modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Text(
                if (parentBalanceId != null) "Define el monto que apartarás de tu saldo padre."
                else "Define tu saldo inicial para empezar a organizar tu dinero.",
                style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = FondlySpacing.md)
            )

            GlassSurface {
                Column(Modifier.padding(FondlySpacing.lg)) {
                    FormField(uiState.name, viewModel::onNameChange, "Nombre",
                        capitalization = KeyboardCapitalization.Sentences)
                    FormField(uiState.targetAmount, viewModel::onTargetAmountChange, "Monto objetivo",
                        keyboardType = KeyboardType.Decimal, modifier = Modifier.padding(top = FondlySpacing.md))
                    FormField(uiState.description, viewModel::onDescriptionChange, "Descripción (opcional)",
                        capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done,
                        modifier = Modifier.padding(top = FondlySpacing.md))

                    FondlyDropdownField(
                        label = "Tipo",
                        options = labels.types.map(labels::typeLabel),
                        selectedIndex = labels.types.indexOf(uiState.type),
                        onSelected = { viewModel.onTypeChange(labels.types[it]) },
                        modifier = Modifier.padding(top = FondlySpacing.md)
                    )

                    FondlyDropdownField(
                        label = "Periodicidad de renovación",
                        options = labels.periodicities.map(labels::periodicityLabel),
                        selectedIndex = labels.periodicities.indexOf(uiState.periodicity),
                        onSelected = { viewModel.onPeriodicityChange(labels.periodicities[it]) },
                        modifier = Modifier.padding(top = FondlySpacing.md)
                    )

                    val hasPeriodicity = uiState.periodicity != lat.virgotp.fondly.domain.model.Periodicity.NONE

                    FondlyDateField(
                        label = "Fecha de renovación",
                        date = uiState.renewalDate,
                        onDateChange = viewModel::onRenewalDateChange,
                        enabled = hasPeriodicity,
                        modifier = Modifier.padding(top = FondlySpacing.md)
                    )

                    FondlyDropdownField(
                        label = "Al renovar (sobrante)",
                        options = labels.rollovers.map(labels::rolloverLabel),
                        selectedIndex = labels.rollovers.indexOf(uiState.rolloverStrategy),
                        onSelected = { viewModel.onRolloverChange(labels.rollovers[it]) },
                        enabled = hasPeriodicity,
                        modifier = Modifier.padding(top = FondlySpacing.md)
                    )

                    FondlyDropdownField(
                        label = "Estrategia de reajuste",
                        options = labels.rebalances.map(labels::rebalanceLabel),
                        selectedIndex = labels.rebalances.indexOf(uiState.rebalanceStrategy),
                        onSelected = { viewModel.onRebalanceChange(labels.rebalances[it]) },
                        modifier = Modifier.padding(top = FondlySpacing.md)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = FondlySpacing.sm)
                    ) {
                        Checkbox(
                            checked = uiState.allowOverdraft,
                            onCheckedChange = viewModel::onAllowOverdraftChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = scheme.primary,
                                checkmarkColor = scheme.onPrimary
                            )
                        )
                        Text("Permitir sobregiro", style = MaterialTheme.typography.bodyMedium, color = scheme.onSurface)
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
            FondlyButton(text = "Guardar", onClick = { viewModel.onSaveClick(parentBalanceId) }, isLoading = uiState.isSaving)
        }
    }
}