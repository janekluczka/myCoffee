package com.luczka.mycoffee.ui.screens.methods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.luczka.mycoffee.ui.components.cards.MethodCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodsScreen(
    widthSizeClass: WindowWidthSizeClass,
    uiState: MethodsUiState,
    onAction: (MethodsAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState.isError) {
            val result = snackbarHostState.showSnackbar(
                message = "Error",
                actionLabel = "Retry",
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    /* Handle snackbar action performed */
                }

                SnackbarResult.Dismissed -> {

                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Recipe categories",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                Divider()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    when (uiState) {
                        is MethodsUiState.HasMethods -> {
                            items(
                                items = uiState.methods,
                                key = { it.id }
                            ) { methodCardUiState ->
                                MethodCard(
                                    modifier = Modifier.animateItem(),
                                    methodUiState = methodCardUiState,
                                    onClick = {
                                        val action = MethodsAction.NavigateToRecipes(methodCardUiState)
                                        onAction(action)
                                    }
                                )
                            }
                        }

                        else -> {

                        }
                    }
                }
            }
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}