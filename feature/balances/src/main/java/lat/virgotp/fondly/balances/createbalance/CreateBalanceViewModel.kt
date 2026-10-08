package lat.virgotp.fondly.balances.createbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lat.virgotp.fondly.application.usecase.CreateBalanceUseCase
import lat.virgotp.fondly.balances.form.BalanceFormError
import lat.virgotp.fondly.balances.form.toFormError
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy

@HiltViewModel
class CreateBalanceViewModel @Inject constructor(
    private val createBalance: CreateBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBalanceUiState())
    val uiState: StateFlow<CreateBalanceUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v, error = null) }
    fun onTargetAmountChange(v: String) = _uiState.update { it.copy(targetAmount = v.replace(",", "."), error = null) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v) }
    fun onTypeChange(v: BalanceType) = _uiState.update { it.copy(type = v) }
    fun onPeriodicityChange(v: Periodicity) = _uiState.update {
        it.copy(periodicity = v, renewalDate = if (v == Periodicity.NONE) null else it.renewalDate)
    }
    fun onRenewalDateChange(v: kotlinx.datetime.LocalDate) = _uiState.update { it.copy(renewalDate = v) }
    fun onRolloverChange(v: RolloverStrategy) = _uiState.update { it.copy(rolloverStrategy = v) }
    fun onRebalanceChange(v: RebalanceStrategy) = _uiState.update { it.copy(rebalanceStrategy = v) }
    fun onAllowOverdraftChange(v: Boolean) = _uiState.update { it.copy(allowOverdraft = v) }
    fun onNotificationThresholdChange(v: String) = _uiState.update { it.copy(notificationThreshold = v) }

    fun onSaveClick(parentBalanceId: Long?) {
        val state = _uiState.value

        val target = state.targetAmount.toBigDecimalOrNull()
        val threshold = state.notificationThreshold.toIntOrNull()

        when {
            state.name.isBlank() ->
                _uiState.update { it.copy(error = BalanceFormError.EMPTY_NAME) }
            target == null || target.signum() <= 0 ->
                _uiState.update { it.copy(error = BalanceFormError.INVALID_AMOUNT) }
            state.periodicity != Periodicity.NONE && state.renewalDate == null ->
                _uiState.update { it.copy(error = BalanceFormError.MISSING_RENEWAL_DATE) }
            threshold != null && threshold !in 0..100 ->
                _uiState.update { it.copy(error = BalanceFormError.INVALID_THRESHOLD) }
            else -> {
                _uiState.update { it.copy(isSaving = true, error = null) }
                viewModelScope.launch {
                    val result = createBalance(
                        name = state.name.trim(),
                        targetAmount = target,
                        parentBalanceId = parentBalanceId,
                        description = state.description.trim().ifBlank { null },
                        type = state.type,
                        periodicity = state.periodicity,
                        renewalDate = state.renewalDate,
                        rolloverStrategy = state.rolloverStrategy,
                        rebalanceStrategy = state.rebalanceStrategy,
                        allowOverdraft = state.allowOverdraft,
                        notificationThreshold = threshold
                    )
                    result.onSuccess {
                        _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
                    }.onFailure { e ->
                        _uiState.update { it.copy(isSaving = false, error = e.toFormError()) }
                    }
                }
            }
        }
    }
}