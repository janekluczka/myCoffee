package com.luczka.mycoffee.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.luczka.mycoffee.ui.models.CategoryUiState
import com.luczka.mycoffee.ui.models.RecipeUiState
import com.luczka.mycoffee.ui.screens.brews.BrewsOneTimeEvent
import com.luczka.mycoffee.ui.screens.brews.BrewsScreen
import com.luczka.mycoffee.ui.screens.brews.BrewsViewModel
import com.luczka.mycoffee.ui.screens.coffees.CoffeesOneTimeEvent
import com.luczka.mycoffee.ui.screens.coffees.CoffeesScreen
import com.luczka.mycoffee.ui.screens.coffees.CoffeesViewModel
import com.luczka.mycoffee.ui.screens.equipments.EquipmentsAction
import com.luczka.mycoffee.ui.screens.equipments.EquipmentsScreen
import com.luczka.mycoffee.ui.screens.home.HomeOneTimeEvent
import com.luczka.mycoffee.ui.screens.home.HomeScreen
import com.luczka.mycoffee.ui.screens.home.HomeViewModel
import com.luczka.mycoffee.ui.screens.recipecategories.RecipeCategoriesOneTimeEvent
import com.luczka.mycoffee.ui.screens.recipecategories.RecipeCategoriesScreen
import com.luczka.mycoffee.ui.screens.recipecategories.RecipeCategoriesViewModel
import com.luczka.mycoffee.ui.screens.recipedetails.RecipeDetailsOneTimeEvent
import com.luczka.mycoffee.ui.screens.recipedetails.RecipeDetailsScreen
import com.luczka.mycoffee.ui.screens.recipedetails.RecipeDetailsViewModel
import com.luczka.mycoffee.ui.screens.recipedetails.RecipeDetailsViewModelFactory
import com.luczka.mycoffee.ui.screens.recipelist.RecipeListOneTimeEvent
import com.luczka.mycoffee.ui.screens.recipelist.RecipesListScreen
import com.luczka.mycoffee.ui.screens.recipelist.RecipesListViewModel
import com.luczka.mycoffee.ui.screens.recipelist.RecipeListViewModelFactory
import kotlin.reflect.typeOf

@Composable
fun MyCoffeeNestedNavHost(
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier,
    navController: NavHostController,
    navigateToAssistant: () -> Unit,
    navigateToBrewDetails: (brewId: Long) -> Unit,
    navigateToCoffeeDetails: (coffeeId: Long) -> Unit,
    navigateToCoffeeInput: (coffeeId: Long?) -> Unit,
    navigateToEquipmentInput: (equipmentId: Long?) -> Unit
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = NestedNavHostRoutes.Home
    ) {
        composable<NestedNavHostRoutes.Home> {
            val viewModel = hiltViewModel<HomeViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.oneTimeEvent.collect { event ->
                    when (event) {
                        HomeOneTimeEvent.NavigateToBrewAssistant -> navigateToAssistant()
                        is HomeOneTimeEvent.NavigateToBrewDetails -> navigateToBrewDetails(event.brewId)
                        is HomeOneTimeEvent.NavigateToCoffeeDetails -> navigateToCoffeeDetails(event.coffeeId)
                    }
                }
            }

            HomeScreen(
                widthSizeClass = widthSizeClass,
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }
        composable<NestedNavHostRoutes.Brews> {
            val viewModel = hiltViewModel<BrewsViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.oneTimeEvent.collect { event ->
                    when (event) {
                        BrewsOneTimeEvent.NavigateToAssistant -> navigateToAssistant()
                        is BrewsOneTimeEvent.NavigateToBrewDetails -> navigateToBrewDetails(event.brewId)
                    }
                }
            }

            BrewsScreen(
                widthSizeClass = widthSizeClass,
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }

        composable<NestedNavHostRoutes.Coffees> {
            val viewModel = hiltViewModel<CoffeesViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.oneTimeEvent.collect { event ->
                    when (event) {
                        CoffeesOneTimeEvent.NavigateUp -> navController.navigateUp()
                        CoffeesOneTimeEvent.NavigateToAddCoffee -> navigateToCoffeeInput(null)
                        is CoffeesOneTimeEvent.NavigateToCoffeeDetails -> navigateToCoffeeDetails(event.coffeeId)
                        is CoffeesOneTimeEvent.NavigateToEditCoffee -> navigateToCoffeeInput(event.coffeeId)
                    }
                }
            }

            CoffeesScreen(
                widthSizeClass = widthSizeClass,
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }

        composable<NestedNavHostRoutes.Equipment> {
            EquipmentsScreen(
                onAction = { action ->
                    when(action) {
                        EquipmentsAction.NavigateToEquipmentInput -> navigateToEquipmentInput(null)
                    }
                }
            )
        }
        composable<NestedNavHostRoutes.Recipes.Categories> {
            val viewModel = hiltViewModel<RecipeCategoriesViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.oneTimeEvent.collect { event ->
                    when (event) {
                        is RecipeCategoriesOneTimeEvent.NavigateToMethodDetails -> navController.navigateToRecipesList(event.categoryUiState)
                    }
                }
            }

            RecipeCategoriesScreen(
                widthSizeClass = widthSizeClass,
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }
        composable<NestedNavHostRoutes.Recipes.List>(
            typeMap = mapOf(
                typeOf<CategoryUiState>() to CustomNavTypes.CategoryUiStateNavType
            )
        ) { backStackEntry ->
            val arguments = backStackEntry.toRoute<NestedNavHostRoutes.Recipes.List>()
            val viewModel = hiltViewModel<RecipesListViewModel, RecipeListViewModelFactory> { factory ->
                factory.create(arguments.categoryUiState)
            }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.oneTimeEvent.collect { event ->
                    when (event) {
                        RecipeListOneTimeEvent.NavigateUp -> navController.navigateUp()
                        is RecipeListOneTimeEvent.NavigateToRecipeDetails -> navController.navigateToRecipesDetails(event.recipeUiState)
                    }
                }
            }

            RecipesListScreen(
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }
        composable<NestedNavHostRoutes.Recipes.Details>(
            typeMap = mapOf(
                typeOf<RecipeUiState>() to CustomNavTypes.RecipeUiStateNavType
            )
        ) { backStackEntry ->
            val localUriHandler = LocalUriHandler.current
            val arguments = backStackEntry.toRoute<NestedNavHostRoutes.Recipes.Details>()
            val viewModel = hiltViewModel<RecipeDetailsViewModel, RecipeDetailsViewModelFactory> { factory ->
                factory.create(arguments.recipeUiState)
            }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.oneTimeEvent.collect { event ->
                    when (event) {
                        RecipeDetailsOneTimeEvent.NavigateUp -> navController.navigateUp()
                        is RecipeDetailsOneTimeEvent.OpenBrowser -> localUriHandler.openUri(event.uri)
                    }
                }
            }

            RecipeDetailsScreen(
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }
    }
}

private fun NavHostController.navigateToRecipesList(categoryUiState: CategoryUiState) {
    navigate(NestedNavHostRoutes.Recipes.List(categoryUiState))
}

private fun NavHostController.navigateToRecipesDetails(recipeUiState: RecipeUiState) {
    navigate(NestedNavHostRoutes.Recipes.Details(recipeUiState))
}