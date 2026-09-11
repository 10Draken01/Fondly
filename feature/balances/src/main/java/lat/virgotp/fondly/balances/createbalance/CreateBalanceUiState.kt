package lat.virgotp.fondly.feature.balances.createbalance

data class CreateBalanceUiState(
    val name: String = "",
    val targetAmount: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
)