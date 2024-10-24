package com.luczka.mycoffee.ui.components.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luczka.mycoffee.ui.models.StepUiState
import com.luczka.mycoffee.ui.theme.MyCoffeeTheme

@Composable
fun RecipeDetailsBrewingStepListItem(stepUiState: StepUiState) {
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${stepUiState.number}")
            }
        },
        headlineContent = {
            Text(text = stepUiState.description)
        },
        trailingContent = {
            stepUiState.time?.let { Text(text = it) }
        }
    )
}

@Preview
@Composable
fun LightThemeBrewingStepListItemPreview() {
    BrewingStepListItemPreview(darkTheme = false)
}

@Preview
@Composable
fun LightThemeBrewingStepListItemWithoutTrailingContentPreview() {
    BrewingStepListItemWithoutTrailingContentPreview(darkTheme = false)
}

@Preview
@Composable
fun DarkThemeBrewingStepListItemPreview() {
    BrewingStepListItemPreview(darkTheme = true)
}

@Preview
@Composable
fun DarkThemeBrewingStepListItemWithoutTrailingContentPreview() {
    BrewingStepListItemWithoutTrailingContentPreview(darkTheme = true)
}

@Composable
private fun BrewingStepListItemPreview(darkTheme: Boolean) {
    val stepUiState = StepUiState(
        number = 1,
        description = "Grind coffee beans (15 grams) relatively fine",
        time = "30-150 s"
    )
    MyCoffeeTheme(darkTheme = darkTheme) {
        RecipeDetailsBrewingStepListItem(stepUiState = stepUiState)
    }
}

@Composable
private fun BrewingStepListItemWithoutTrailingContentPreview(darkTheme: Boolean) {
    val stepUiState = StepUiState(
        number = 1,
        description = "Grind coffee beans (15 grams) relatively fine",
        time = null,
    )
    MyCoffeeTheme(darkTheme = darkTheme) {
        RecipeDetailsBrewingStepListItem(stepUiState = stepUiState)
    }
}