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
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class EditBalanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBalanceById: GetBalanceByIdUseCase,
    private val updateBalance: UpdateBalanceUseCase
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
                targetAmount = balance.targetAmount.toString(),
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
            EditBalanceUiState(isLoading = false, errorMessage = "No se encontró el saldo")
        }
    }

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v, errorMessage = null) }
    fun onTargetAmountChange(v: String) = _uiState.update { it.copy(targetAmount = v, errorMessage = null) }
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
    fun onIsActiveChange(v: Boolean) = _uiState.update { it.copy(isActive = v) }

    fun onSaveClick() {
        val state = _uiState.value
        val balance = original ?: return
        val target = state.targetAmount.replace(",", "").toDoubleOrNull()
        val threshold = state.notificationThreshold.toIntOrNull()

        when {
            state.name.isBlank() -> _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            target == null || target <= 0 -> _uiState.update { it.copy(errorMessage = "Ingresa un monto objetivo válido") }
            state.periodicity != Periodicity.NONE && state.renewalDate == null ->
                _uiState.update { it.copy(errorMessage = "Selecciona la fecha de renovación") }
            threshold != null && threshold !in 0..100 ->
                _uiState.update { it.copy(errorMessage = "El umbral debe estar entre 0 y 100") }
            else -> {
                _uiState.update { it.copy(isSaving = true, errorMessage = null) }
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
                        _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
                    }.onFailure { e ->
                        _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                    }
                }
            }
        }
    }
}