package com.coffee.mycoffeeassistant.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AddCoffeeFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Filled.Add, contentDescription = null)
    }
}

@Preview
@Composable
fun AddCoffeeFloatingActionButtonPreview() {
    AddCoffeeFloatingActionButton {}
}