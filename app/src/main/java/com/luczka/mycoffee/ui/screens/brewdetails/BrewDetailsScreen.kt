package com.luczka.mycoffee.ui.screens.brewdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.components.dialogs.DeleteBrewDialog
import com.luczka.mycoffee.ui.components.icons.ArrowBackIcon
import com.luczka.mycoffee.ui.components.icons.DeleteIcon
import com.luczka.mycoffee.ui.components.listitem.BrewDetailsParametersListItem
import com.luczka.mycoffee.ui.components.listitem.HistoryDetailsCoffeeListItem
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewDetailsScreen(
    brewDetailsUiState: BrewDetailsUiState,
    onAction: (BrewDetailsAction) -> Unit
) {
    brewDetailsUiState.brew ?: return

    var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (openDeleteDialog) {
        DeleteBrewDialog(
            brewUiState = brewDetailsUiState.brew,
            onNegative = {
                openDeleteDialog = false
            },
            onPositive = {
                openDeleteDialog = false
                val action = BrewDetailsAction.OnDeleteClicked
                onAction(action)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val action = BrewDetailsAction.NavigateUp
                            onAction(action)
                        }
                    ) {
                        ArrowBackIcon()
                    }
                },
                title = {
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                    val date = brewDetailsUiState.brew.addedOn.format(formatter)
                    Text(
                        text = date,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { openDeleteDialog = true }) {
                        DeleteIcon()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Divider()
            LazyColumn(contentPadding = PaddingValues(vertical = 16.dp)) {
                brewDetailsUiState.brew.let { brewUiState ->

                    item {
                        Text(
                            modifier = Modifier.padding(
                                top = 0.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 8.dp
                            ),
                            text = if (brewUiState.brewedCoffees.size > 1) {
                                stringResource(id = R.string.selected_coffees)
                            } else {
                                stringResource(id = R.string.selected_coffee)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                    item {
                        Column {
                            if (brewUiState.brewedCoffees.isEmpty()) {
                                HistoryDetailsCoffeeListItem(coffeeUiState = null)
                            } else {
                                brewUiState.brewedCoffees.forEach { brewedCoffeeUiState ->
                                    HistoryDetailsCoffeeListItem(coffeeUiState = brewedCoffeeUiState.coffee)
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    item {
                        Text(
                            modifier = Modifier.padding(
                                top = 0.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 8.dp
                            ),
                            text = stringResource(id = R.string.assistant_selected_parameters),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                    item {
                        BrewDetailsParametersListItem(
                            index = 0,
                            headlineText = stringResource(id = R.string.ratio),
                            trailingText = stringResource(
                                id = R.string.format_ratio,
                                brewUiState.coffeeRatio,
                                brewUiState.waterRatio
                            )
                        )
                        BrewDetailsParametersListItem(
                            index = 1,
                            headlineText = stringResource(id = R.string.coffee),
                            trailingText = stringResource(
                                id = R.string.format_coffee_amount_grams,
                                brewUiState.coffeeAmount
                            )
                        )
                        BrewDetailsParametersListItem(
                            index = 2,
                            headlineText = stringResource(id = R.string.water),
                            trailingText = stringResource(
                                id = R.string.format_coffee_amount_grams,
                                brewUiState.waterAmount
                            )
                        )
                    }
                }
            }
        }
    }
}