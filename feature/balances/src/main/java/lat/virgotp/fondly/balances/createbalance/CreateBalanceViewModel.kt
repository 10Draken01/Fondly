package lat.virgotp.fondly.feature.balances.createbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lat.virgotp.fondly.application.usecase.CreateBalanceUseCase
import javax.inject.Inject

@HiltViewModel
class CreateBalanceViewModel @Inject constructor(
    private val createBalance: CreateBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBalanceUiState())
    val uiState: StateFlow<CreateBalanceUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun onTargetAmountChange(value: String) {
        _uiState.update { it.copy(targetAmount = value, errorMessage = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onSaveClick() {
        val state = _uiState.value
        val amount = state.targetAmount.toDoubleOrNull()

        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return
        }
        if (amount == null || amount < 0) {
            _uiState.update { it.copy(errorMessage = "Ingresa un monto válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = createBalance(
                name = state.name,
                targetAmount = amount,
                description = state.description.ifBlank { null }
            )
            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Error al guardar")
                    }
                }
        }
    }
}