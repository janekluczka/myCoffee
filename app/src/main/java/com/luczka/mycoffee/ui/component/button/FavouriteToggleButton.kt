package com.luczka.mycoffee.ui.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.luczka.mycoffee.R

@Composable
fun FavouriteToggleButton(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    IconButton(
        modifier = modifier,
        onClick = onCheckedChange,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = contentColor
        )
    ) {
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.favorite_24px),
                contentDescription = null
            )
        } else {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = null
            )
        }
    }
}