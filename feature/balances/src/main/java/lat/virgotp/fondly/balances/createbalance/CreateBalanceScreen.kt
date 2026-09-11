package lat.virgotp.fondly.feature.balances.createbalance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CreateBalanceScreen(
    onSaved: () -> Unit,
    viewModel: CreateBalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            onSaved()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.targetAmount,
            onValueChange = viewModel::onTargetAmountChange,
            label = { Text("Monto objetivo") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        uiState.errorMessage?.let { error ->
            Text(text = error, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = viewModel::onSaveClick,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(if (uiState.isSaving) "Guardando..." else "Guardar")
        }
    }
}