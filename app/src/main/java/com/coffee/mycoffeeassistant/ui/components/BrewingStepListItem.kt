package com.coffee.mycoffeeassistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffee.mycoffeeassistant.ui.model.components.StepUiState
import com.coffee.mycoffeeassistant.ui.theme.MyCoffeeAssistantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewingStepListItem(stepUiState: StepUiState) {
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${stepUiState.number}")
            }
        },
        headlineText = { Text(text = stepUiState.description) },
        trailingContent = { stepUiState.time?.let { Text(text = it) } }
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
    MyCoffeeAssistantTheme(darkTheme = darkTheme) {
        BrewingStepListItem(stepUiState = stepUiState)
    }
}

@Composable
private fun BrewingStepListItemWithoutTrailingContentPreview(darkTheme: Boolean) {
    val stepUiState = StepUiState(
        number = 1,
        description = "Grind coffee beans (15 grams) relatively fine",
    )
    MyCoffeeAssistantTheme(darkTheme = darkTheme) {
        BrewingStepListItem(stepUiState = stepUiState)
    }
}