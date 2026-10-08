package lat.virgotp.fondly.balances.createbalance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import lat.virgotp.fondly.balances.components.BalanceFieldLabels
import lat.virgotp.fondly.balances.form.BalanceFormError
import lat.virgotp.fondly.balances.form.balanceFormErrorText
import lat.virgotp.fondly.feature.balances.R
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.ui_common.atoms.ConfirmationDialog
import lat.virgotp.fondly.ui_common.atoms.FondlyButton
import lat.virgotp.fondly.ui_common.form.FormAmountField
import lat.virgotp.fondly.ui_common.form.FormCheckRow
import lat.virgotp.fondly.ui_common.form.FormDateField
import lat.virgotp.fondly.ui_common.form.FormDropdownField
import lat.virgotp.fondly.ui_common.form.FormSection
import lat.virgotp.fondly.ui_common.form.FormTextField
import lat.virgotp.fondly.ui_common.templates.FondlyScreenScaffold
import lat.virgotp.fondly.ui_common.theme.FondlyCurrencyController
import lat.virgotp.fondly.ui_common.theme.FondlySpacing

/** Formulario de creación de Balance (RF-001/RF-039), componentes de formulario específicos. */
@Composable
fun CreateBalanceScreen(
    parentBalanceId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: CreateBalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currency = FondlyCurrencyController.rememberCurrency()
    val labels = BalanceFieldLabels

    LaunchedEffect(uiState.savedSuccessfully) { if (uiState.savedSuccessfully) onSaved() }

    val isSection = parentBalanceId != null
    val title = stringResource(if (isSection) R.string.create_section_title else R.string.create_balance_title)

    // Hay riesgo de pérdida de datos solo si el usuario ya escribió algo.
    val hasUnsavedChanges = uiState.name.isNotBlank() ||
        uiState.targetAmount.isNotBlank() || uiState.description.isNotBlank()
    var showDiscardDialog by remember { mutableStateOf(false) }
    val attemptBack = { if (hasUnsavedChanges) showDiscardDialog = true else onBack() }
    BackHandler(enabled = true) { attemptBack() }

    FondlyScreenScaffold(
        title = title,
        navigationIcon = {
            IconButton(onClick = attemptBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back)
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(FondlySpacing.lg)
        ) {
            Text(
                stringResource(
                    if (isSection) R.string.create_section_subtitle else R.string.create_balance_subtitle
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = FondlySpacing.lg)
            )

            // — Sección: información general —
            FormSection(title = stringResource(R.string.section_general)) {
                FormTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = stringResource(R.string.field_name),
                    placeholder = stringResource(R.string.field_name_placeholder),
                    errorMessage = uiState.error
                        .takeIf { it == BalanceFormError.EMPTY_NAME }
                        ?.let { balanceFormErrorText(it) }
                )
                FormAmountField(
                    value = uiState.targetAmount,
                    onValueChange = viewModel::onTargetAmountChange,
                    label = stringResource(R.string.field_target_amount),
                    currencySymbol = currency.symbol,
                    placeholder = stringResource(R.string.field_target_amount_placeholder),
                    errorMessage = uiState.error
                        .takeIf { it == BalanceFormError.INVALID_AMOUNT }
                        ?.let { balanceFormErrorText(it) },
                    modifier = Modifier.padding(top = FondlySpacing.md)
                )
                FormTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = stringResource(R.string.field_description),
                    placeholder = stringResource(R.string.field_description_placeholder),
                    imeAction = ImeAction.Done,
                    modifier = Modifier.padding(top = FondlySpacing.md)
                )
                FormDropdownField(
                    label = stringResource(R.string.field_type),
                    options = labels.types.map { labels.typeLabel(it) },
                    selectedIndex = labels.types.indexOf(uiState.type),
                    onSelected = { viewModel.onTypeChange(labels.types[it]) },
                    modifier = Modifier.padding(top = FondlySpacing.md)
                )
            }

            Spacer(Modifier.height(FondlySpacing.lg))

            // — Sección: renovación —
            FormSection(title = stringResource(R.string.section_renewal)) {
                val hasPeriodicity = uiState.periodicity != Periodicity.NONE

                FormDropdownField(
                    label = stringResource(R.string.field_periodicity),
                    options = labels.periodicities.map { labels.periodicityLabel(it) },
                    selectedIndex = labels.periodicities.indexOf(uiState.periodicity),
                    onSelected = { viewModel.onPeriodicityChange(labels.periodicities[it]) }
                )
                FormDateField(
                    label = stringResource(R.string.field_renewal_date),
                    date = uiState.renewalDate,
                    onDateChange = viewModel::onRenewalDateChange,
                    enabled = hasPeriodicity,
                    errorMessage = uiState.error
                        .takeIf { it == BalanceFormError.MISSING_RENEWAL_DATE }
                        ?.let { balanceFormErrorText(it) },
                    modifier = Modifier.padding(top = FondlySpacing.md)
                )
                FormDropdownField(
                    label = stringResource(R.string.field_rollover),
                    options = labels.rollovers.map { labels.rolloverLabel(it) },
                    selectedIndex = labels.rollovers.indexOf(uiState.rolloverStrategy),
                    onSelected = { viewModel.onRolloverChange(labels.rollovers[it]) },
                    enabled = hasPeriodicity,
                    modifier = Modifier.padding(top = FondlySpacing.md)
                )
                FormDropdownField(
                    label = stringResource(R.string.field_rebalance),
                    options = labels.rebalances.map { labels.rebalanceLabel(it) },
                    selectedIndex = labels.rebalances.indexOf(uiState.rebalanceStrategy),
                    onSelected = { viewModel.onRebalanceChange(labels.rebalances[it]) },
                    modifier = Modifier.padding(top = FondlySpacing.md)
                )
            }

            Spacer(Modifier.height(FondlySpacing.lg))

            // — Sección: opciones —
            FormSection(title = stringResource(R.string.section_options)) {
                FormCheckRow(
                    checked = uiState.allowOverdraft,
                    onCheckedChange = viewModel::onAllowOverdraftChange,
                    label = stringResource(R.string.field_overdraft),
                    supportingText = stringResource(R.string.field_overdraft_hint)
                )
                FormTextField(
                    value = uiState.notificationThreshold,
                    onValueChange = viewModel::onNotificationThresholdChange,
                    label = stringResource(R.string.field_threshold),
                    placeholder = "80",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None,
                    errorMessage = uiState.error
                        .takeIf { it == BalanceFormError.INVALID_THRESHOLD }
                        ?.let { balanceFormErrorText(it) },
                    imeAction = ImeAction.Done,
                    modifier = Modifier.padding(top = FondlySpacing.sm)
                )
            }

            // Error global (jerarquía/persistencia), no ligado a un campo.
            uiState.error
                ?.takeIf {
                    it in setOf(
                        BalanceFormError.PARENT_EXCEEDED,
                        BalanceFormError.PARENT_NOT_FOUND,
                        BalanceFormError.CYCLE_NOT_ALLOWED,
                        BalanceFormError.UNKNOWN
                    )
                }
                ?.let { error ->
                    Text(
                        balanceFormErrorText(error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = FondlySpacing.md)
                    )
                }

            Spacer(Modifier.height(FondlySpacing.lg))
            FondlyButton(
                text = stringResource(R.string.action_save),
                onClick = { viewModel.onSaveClick(parentBalanceId) },
                isLoading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    ConfirmationDialog(
        show = showDiscardDialog,
        title = stringResource(R.string.dialog_discard_title),
        message = stringResource(R.string.dialog_discard_message),
        confirmLabel = stringResource(R.string.action_discard),
        dismissLabel = stringResource(R.string.action_keep_editing),
        onConfirm = { showDiscardDialog = false; onBack() },
        onDismiss = { showDiscardDialog = false }
    )
}
