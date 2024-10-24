package com.luczka.mycoffee.ui.screens.coffeedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.components.icons.ArrowBackIcon
import com.luczka.mycoffee.ui.components.icons.DeleteIcon
import com.luczka.mycoffee.ui.components.icons.EditIcon
import com.luczka.mycoffee.ui.components.icons.FavouriteIcon
import com.luczka.mycoffee.ui.models.CoffeeUiState
import com.luczka.mycoffee.util.isPositiveFloat
import java.io.File

@Composable
fun CoffeeDetailsScreen(
    widthSizeClass: WindowWidthSizeClass,
    uiState: CoffeeDetailsUiState,
    onAction: (CoffeeDetailsAction) -> Unit
) {
    when (uiState) {
        is CoffeeDetailsUiState.NoCoffee -> {
            LaunchedEffect(uiState.isDeleted) {
                if (uiState.isDeleted) {
                    val action = CoffeeDetailsAction.NavigateUp
                    onAction(action)
                }
            }
        }

        is CoffeeDetailsUiState.HasCoffee -> {
            var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

            if (openDeleteDialog) {
                DeleteCoffeeDialog(
                    coffeeUiState = uiState.coffee,
                    onNegative = {
                        openDeleteDialog = false
                    },
                    onPositive = {
                        openDeleteDialog = false
                        val action = CoffeeDetailsAction.OnDeleteClicked
                        onAction(action)
                    }
                )
            }

            Scaffold(
                topBar = {
                    CoffeeDetailsTopBar(
                        coffeeUiState = uiState.coffee,
                        onAction = onAction,
                        onShowDeleteDialog = { openDeleteDialog = true }
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Divider()
                    CoffeeDetailsList(coffeeUiState = uiState.coffee)
                }
            }
        }
    }
}

@Composable
private fun CoffeeDetailsList(coffeeUiState: CoffeeUiState) {
    val filesDir = LocalContext.current.filesDir

    LazyColumn(contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)) {
        item {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f / 1f)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (coffeeUiState.imageFile960x960 != null) {
                    val cacheFile = File(filesDir, coffeeUiState.imageFile960x960)
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(cacheFile)
                        .build()
                    AsyncImage(
                        model = model,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_photo_camera),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
        item {
            CoffeeDetailListItem(
                supportingText = stringResource(id = R.string.coffee_parameters_name),
                headlineText = coffeeUiState.name
            )
        }
        item {
            CoffeeDetailListItem(
                supportingText = stringResource(id = R.string.coffee_parameters_brand),
                headlineText = coffeeUiState.brand
            )
        }
        if (coffeeUiState.amount?.isPositiveFloat() == true) {
            item {
                CoffeeDetailListItem(
                    supportingText = stringResource(id = R.string.coffee_parameters_amount),
                    headlineText = coffeeUiState.amount + " " + stringResource(id = R.string.unit_gram_short)
                )
            }
        }
        if (coffeeUiState.roast != null) {
            item {
                CoffeeDetailListItem(
                    supportingText = stringResource(id = R.string.coffee_parameters_roast),
                    headlineText = stringResource(id = coffeeUiState.roast.stringRes)
                )
            }
        }
        if (coffeeUiState.process != null) {
            item {
                CoffeeDetailListItem(
                    supportingText = stringResource(id = R.string.coffee_parameters_process),
                    headlineText = stringResource(id = coffeeUiState.process.stringRes)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CoffeeDetailsTopBar(
    coffeeUiState: CoffeeUiState,
    onAction: (CoffeeDetailsAction) -> Unit,
    onShowDeleteDialog: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = {
                    val action = CoffeeDetailsAction.NavigateUp
                    onAction(action)
                }
            ) {
                ArrowBackIcon()
            }
        },
        title = {
            Text(
                text = coffeeUiState.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(
                onClick = {
                    val action = CoffeeDetailsAction.OnFavouriteClicked
                    onAction(action)
                }
            ) {
                if(coffeeUiState.isFavourite) {
                    FavouriteIcon()
                } else {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }
            IconButton(
                onClick = {
                    val action = CoffeeDetailsAction.OnEditClicked(coffeeId = coffeeUiState.coffeeId)
                    onAction(action)
                }
            ) {
                EditIcon()
            }
            IconButton(onClick = onShowDeleteDialog) {
                DeleteIcon()
            }
        }
    )
}

@Composable
private fun CoffeeDetailListItem(
    supportingText: String,
    headlineText: String
) {
    ListItem(
        headlineContent = {
            Text(
                text = headlineText,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

//@Preview
//@Composable
//fun CoffeeDetailsPreview() {
//    val coffeeUiState = CoffeeUiState(
//        name = "ethiopia sami",
//        brand = "monko.",
//        amount = "240.0",
//        roast = Roast.MediumDark,
//        process = Process.Natural
//    )
//    MyCoffeeTheme {
//        CoffeeDetailsScreen(
//            widthSizeClass = WindowWidthSizeClass.Compact,
//            coffeeUiState = coffeeUiState,
//            navigateUp = {},
//            onUpdateFavourite = {},
//            onEdit = {},
//            onDelete = { _ -> }
//        )
//    }
//}