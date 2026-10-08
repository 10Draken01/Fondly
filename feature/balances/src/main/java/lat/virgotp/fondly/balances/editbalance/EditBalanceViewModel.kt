package lat.virgotp.fondly.balances.editbalance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lat.virgotp.fondly.application.usecase.GetBalanceByIdUseCase
import lat.virgotp.fondly.balances.form.BalanceFormError
import lat.virgotp.fondly.balances.form.toFormError
import lat.virgotp.fondly.application.usecase.ResetBalanceByIdUseCase
import lat.virgotp.fondly.application.usecase.UpdateBalanceUseCase
import lat.virgotp.fondly.domain.model.Balance
import lat.virgotp.fondly.domain.model.BalanceType
import lat.virgotp.fondly.domain.model.Periodicity
import lat.virgotp.fondly.domain.model.RebalanceStrategy
import lat.virgotp.fondly.domain.model.RolloverStrategy

data class EditBalanceUiState(
    val name: String = "",
    val targetAmount: String = "",
    val description: String = "",
    val type: BalanceType = BalanceType.REGULAR,
    val periodicity: Periodicity = Periodicity.NONE,
    val renewalDate: kotlinx.datetime.LocalDate? = null,
    val rolloverStrategy: RolloverStrategy = RolloverStrategy.RESET,
    val rebalanceStrategy: RebalanceStrategy = RebalanceStrategy.EVEN,
    val allowOverdraft: Boolean = false,
    val notificationThreshold: String = "",
    val isActive: Boolean = true,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: BalanceFormError? = null,
    val savedSuccessfully: Boolean = false,
    // valor para renovar el balance
    val resetBalanceAndChildren: Boolean = false,
)

@HiltViewModel
class EditBalanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBalanceById: GetBalanceByIdUseCase,
    private val updateBalance: UpdateBalanceUseCase,
    private val resetBalanceByIdUseCase: ResetBalanceByIdUseCase
) : ViewModel() {

    private val balanceId: Long = checkNotNull(savedStateHandle["balanceId"])

    private val _uiState = MutableStateFlow(EditBalanceUiState())
    val uiState: StateFlow<EditBalanceUiState> = _uiState.asStateFlow()

    private var original: Balance? = null

    init { load() }

    private fun load() = viewModelScope.launch {
        val balance = getBalanceById(balanceId)
        original = balance
        _uiState.value = if (balance != null) {
            EditBalanceUiState(
                name = balance.name,
                targetAmount = balance.targetAmount.toPlainString(),
                description = balance.description.orEmpty(),
                type = balance.type,
                periodicity = balance.periodicity,
                renewalDate = balance.renewalDate,
                rolloverStrategy = balance.rolloverStrategy,
                rebalanceStrategy = balance.rebalanceStrategy,
                allowOverdraft = balance.allowOverdraft,
                notificationThreshold = balance.notificationThreshold?.toString().orEmpty(),
                isActive = balance.isActive,
                isLoading = false
            )
        } else {
            EditBalanceUiState(isLoading = false, error = BalanceFormError.BALANCE_NOT_FOUND)
        }
    }

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

    fun onResetBalanceAndChildren(v: Boolean) = _uiState.update { it.copy(resetBalanceAndChildren = v) }
    fun onNotificationThresholdChange(v: String) = _uiState.update { it.copy(notificationThreshold = v) }
    fun onIsActiveChange(v: Boolean) = _uiState.update { it.copy(isActive = v) }

    /** true si algún campo difiere del saldo original cargado. */
    fun hasUnsavedChanges(): Boolean = original?.let { o ->
        val s = _uiState.value
        s.name != o.name ||
            s.targetAmount.toBigDecimalOrNull() != o.targetAmount ||
            s.description != o.description.orEmpty() ||
            s.type != o.type ||
            s.periodicity != o.periodicity ||
            s.renewalDate != o.renewalDate ||
            s.rolloverStrategy != o.rolloverStrategy ||
            s.rebalanceStrategy != o.rebalanceStrategy ||
            s.allowOverdraft != o.allowOverdraft ||
            s.isActive != o.isActive ||
            s.notificationThreshold != (o.notificationThreshold?.toString() ?: "")
    } ?: false

    fun onSaveClick() {
        val state = _uiState.value
        val balance = original ?: return
        val target = state.targetAmount.toBigDecimalOrNull()
        val threshold = state.notificationThreshold.toIntOrNull()

        when {
            state.name.isBlank() -> _uiState.update { it.copy(error = BalanceFormError.EMPTY_NAME) }
            target == null || target.signum() <= 0 -> _uiState.update { it.copy(error = BalanceFormError.INVALID_AMOUNT) }
            state.periodicity != Periodicity.NONE && state.renewalDate == null ->
                _uiState.update { it.copy(error = BalanceFormError.MISSING_RENEWAL_DATE) }
            threshold != null && threshold !in 0..100 ->
                _uiState.update { it.copy(error = BalanceFormError.INVALID_THRESHOLD) }
            else -> {
                _uiState.update { it.copy(isSaving = true, error = null) }
                viewModelScope.launch {
                    updateBalance(
                        balance.copy(
                            name = state.name.trim(),
                            targetAmount = target,
                            description = state.description.trim().ifBlank { null },
                            type = state.type,
                            periodicity = state.periodicity,
                            renewalDate = state.renewalDate,
                            rolloverStrategy = state.rolloverStrategy,
                            rebalanceStrategy = state.rebalanceStrategy,
                            allowOverdraft = state.allowOverdraft,
                            notificationThreshold = threshold,
                            isActive = state.isActive
                        )
                    ).onSuccess {
                        if (state.resetBalanceAndChildren) {
                            resetBalanceByIdUseCase(balance.id)
                                .onSuccess {
                                    _uiState.update {
                                        it.copy(
                                            isSaving = false,
                                            savedSuccessfully = true
                                        )
                                    }
                                }
                                .onFailure { e ->
                                    _uiState.update {
                                        it.copy(
                                            isSaving = false,
                                            error = e.toFormError()
                                        )
                                    }
                                }
                        } else {
                            _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
                        }
                    }.onFailure { e ->
                        _uiState.update { it.copy(isSaving = false, error = e.toFormError()) }
                    }
                }
            }
        }
    }
}