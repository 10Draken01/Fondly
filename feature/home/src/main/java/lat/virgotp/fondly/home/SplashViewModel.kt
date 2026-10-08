package lat.virgotp.fondly.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import lat.virgotp.fondly.application.usecase.ProcessBalanceRenewalsUseCase

/** Procesa renovaciones pendientes mientras se muestra el splash (RF-020/RF-030). */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val processBalanceRenewals: ProcessBalanceRenewalsUseCase
) : ViewModel() {
    init {
        viewModelScope.launch { processBalanceRenewals() }
    }
}