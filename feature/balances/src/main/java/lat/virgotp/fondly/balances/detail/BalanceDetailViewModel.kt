package lat.virgotp.fondly.balances.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lat.virgotp.fondly.application.usecase.ActivateBalanceByIdUseCase
import lat.virgotp.fondly.application.usecase.DeactivateBalanceByIdUseCase
import lat.virgotp.fondly.application.usecase.GetBalanceByIdUseCase
import lat.virgotp.fondly.application.usecase.GetChildrenOfUseCase
import lat.virgotp.fondly.application.usecase.ResetBalanceByIdUseCase
import lat.virgotp.fondly.domain.model.Balance

@HiltViewModel
class BalanceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBalanceById: GetBalanceByIdUseCase,
    private val getChildrenOf: GetChildrenOfUseCase,
    private val activateBalanceById: ActivateBalanceByIdUseCase,
    private val deactivateBalanceById: DeactivateBalanceByIdUseCase,
    private val resetBalanceByIdUseCase: ResetBalanceByIdUseCase
) : ViewModel() {

    private val balanceId: Long = checkNotNull(savedStateHandle["balanceId"])

    private val _uiState = MutableStateFlow(BalanceDetailUiState())
    val uiState: StateFlow<BalanceDetailUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    private fun load() = viewModelScope.launch {
        val balance = getBalanceById(balanceId)
        val children = getChildrenOf(balanceId)
        val descendantsByParentId = buildDescendantsMap(balanceId)
        _uiState.value = BalanceDetailUiState(
            balance = balance,
            children = children,
            descendantsByParentId = descendantsByParentId,
            isLoading = false
        )
    }

    /**
     * Recorre TODO el arbol de descendientes (hijos, nietos, bisnietos...)
     * usando el caso de uso existente (que solo trae hijos DIRECTOS de un
     * id dado), y arma un mapa parentId -> hijos directos para cada nodo
     * del arbol. Necesario porque no existe (aun) un caso de uso que
     * traiga el arbol completo de una sola vez.
     */
    private suspend fun buildDescendantsMap(rootId: Long): Map<Long, List<Balance>> {
        val map = mutableMapOf<Long, List<Balance>>()
        suspend fun recurse(parentId: Long) {
            val kids = getChildrenOf(parentId)
            map[parentId] = kids
            kids.forEach { recurse(it.id) }
        }
        recurse(rootId)
        return map
    }

    /** RF-037: reactivar (con sub-árbol) o desactivar desde el detalle. */
    fun onToggleActive() = viewModelScope.launch {
        val balance = _uiState.value.balance ?: return@launch
        val result = if (balance.isActive) {
            deactivateBalanceById(balance.id)
        } else {
            activateBalanceById(balance.id)
        }
        if (result.isSuccess) load()
    }

    /**  */
    fun onClickResetBalance() = viewModelScope.launch {
        val balance = _uiState.value.balance ?: return@launch
        resetBalanceByIdUseCase(balance.id)
    }
}