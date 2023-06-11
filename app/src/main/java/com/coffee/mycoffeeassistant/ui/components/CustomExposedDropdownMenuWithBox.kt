package com.coffee.mycoffeeassistant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomExposedDropdownMenuWithBox(
    value: String,
    label: @Composable (() -> Unit)?,
    menuItems: List<Any>,
    formatItemText: (Any) -> String = { it.toString() },
    onItemSelected: (Any) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = menuExpanded,
        onExpandedChange = { /* Change handled by interactionSource */ },
    ) {
        ClickableOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = value,
            maxLines = 1,
            label = label,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
            onClick = { menuExpanded = !menuExpanded }
        )
        ExposedDropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            menuItems.forEach { selectedItem ->
                val itemText = formatItemText(selectedItem)
                DropdownMenuItem(
                    text = { Text(text = itemText) },
                    onClick = {
                        onItemSelected(selectedItem)
                        menuExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}