package com.luczka.mycoffee.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.models.BrewUiState
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    widthSizeClass: WindowWidthSizeClass,
    uiState: HistoryUiState,
    onAction: (HistoryAction) -> Unit,
) {
    val brewListState = rememberLazyListState()

    Scaffold(
        topBar = {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.app_name_short),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is HistoryUiState.NoBrews -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No coffees brewed yet",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Brew your first coffee using Assistant.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is HistoryUiState.HasBrews -> {
                Column(modifier = Modifier.padding(innerPadding)) {
                    LazyColumn(
                        state = brewListState,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.brews,
                            key = { it.brewId }
                        ) { brewedUiState ->
                            HistoryListItem(
                                brewedUiState = brewedUiState,
                                onClick = {
                                    val action = HistoryAction.NavigateToHistoryDetails(brewedUiState.brewId)
                                    onAction(action)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryListItem(
    brewedUiState: BrewUiState,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val date = brewedUiState.date.format(formatter)

    val headlineText = when (brewedUiState.brewedCoffees.size) {
        0 -> "Unknown"
        1 -> brewedUiState.brewedCoffees.first().let { "${it.coffee.name}, ${it.coffee.brand}" }
        else -> brewedUiState.brewedCoffees.joinToString(separator = " + ") { it.coffee.name }
    }

    ListItem(
        modifier = Modifier.clickable {
            onClick()
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.inverseOnSurface),
                contentAlignment = Alignment.Center
            ) {
                if (brewedUiState.rating != null) {
                    Text(text = "${brewedUiState.rating}")
                }
            }
        },
        overlineContent = {
            Text(
                text = date,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
        },
        headlineContent = {
            Text(
                text = headlineText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = stringResource(
                    id = R.string.coffee_parameters_amount_with_unit,
                    brewedUiState.coffeeAmount
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}