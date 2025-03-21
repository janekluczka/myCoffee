package com.luczka.mycoffee.ui.screens.brewassistant.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.screens.brewassistant.BrewAssistantAction
import com.luczka.mycoffee.ui.screens.brewassistant.BrewAssistantUiState
import com.luczka.mycoffee.ui.screens.brewassistant.components.BrewAssistantSelectionListItem

@Composable
fun AssistantSelectionScreen(
    uiState: BrewAssistantUiState,
    onAction: (BrewAssistantAction) -> Unit
) {
    when (uiState.currentCoffees.size) {
        0 -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.brew_assistant_selection_screen_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No coffees available",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.brew_assistant_selection_screen_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                item {
                    Text(
                        modifier = Modifier.padding(
                            top = 24.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        ),
                        text = "Available coffees",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                items(
                    items = uiState.currentCoffees,
                    key = { it.coffeeId }
                ) { coffeeUiState ->
                    BrewAssistantSelectionListItem(
                        coffeeUiState = coffeeUiState,
                        isSelected = when (uiState) {
                            is BrewAssistantUiState.NoneSelected -> false
                            is BrewAssistantUiState.CoffeeSelected -> uiState.selectedCoffees.containsKey(coffeeUiState)
                        },
                        onClick = {
                            onAction(BrewAssistantAction.OnSelectedCoffeeChanged(it))
                        }
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun AssistantSelectionScreenPreview() {
//    val firstSelectedCoffee = CoffeeUiState(
//        coffeeId = 1,
//        name = "ethiopia sami",
//        brand = "monko.",
//        amount = "250.0"
//    )
//    val secondSelectedCoffee = CoffeeUiState(
//        coffeeId = 2,
//        name = "Kolumbia",
//        brand = "Mała Czarna",
//        amount = "200.0"
//    )
//    val uiState = AssistantUiState.CoffeeSelected(
//        currentCoffees = listOf(firstSelectedCoffee, secondSelectedCoffee),
//        selectedCoffees = mapOf(Pair(firstSelectedCoffee, AmountSelectionUiState()))
//    )
//    AssistantSelectionScreen(uiState = uiState, onAction = {})
//}