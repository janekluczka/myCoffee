package com.luczka.mycoffee.ui.screens.assistant.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.screens.assistant.AssistantUiState

@Composable
fun AssistantFinishDialog(
    uiState: AssistantUiState,
    onNegative: () -> Unit,
    onPositive: () -> Unit,
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.dialog_title_finish)) },
        text = { FinishDialogText(uiState = uiState) },
        onDismissRequest = onNegative,
        dismissButton = {
            TextButton(onClick = onNegative) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onPositive) {
                Text(text = stringResource(id = R.string.dialog_action_finish))
            }
        },
    )
}

@Composable
private fun FinishDialogText(uiState: AssistantUiState) {
    when (uiState) {
        is AssistantUiState.NoneSelected -> {

        }

        is AssistantUiState.CoffeeSelected -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.selectedCoffees.forEach { (selectedCoffee, amountSelectionUiState) ->
                    Text(
                        text = stringResource(
                            id = R.string.dialog_text_finish,
                            amountSelectionUiState.selectedAmount,
                            selectedCoffee.name,
                            selectedCoffee.brand,
                        )
                    )
                }
            }
        }
    }
}