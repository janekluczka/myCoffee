package com.coffee.mycoffeeassistant.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.coffee.mycoffeeassistant.ui.MyCoffeeAssistantAppState
import com.coffee.mycoffeeassistant.ui.screens.addcoffee.AddCoffeeScreen
import com.coffee.mycoffeeassistant.ui.screens.coffeedetails.CoffeeDetailsScreen
import com.coffee.mycoffeeassistant.ui.screens.cupboard.CupboardScreen

fun NavGraphBuilder.cupboardNavGraph(appState: MyCoffeeAssistantAppState) {
    navigation(
        startDestination = Screen.Cupboard.route,
        route = "cupboard"
    ) {
        composable(Screen.Cupboard.route) {
            CupboardScreen(navigateToDetails = {
                appState.navController.navigate(Screen.CoffeeDetails.createRoute(id = it))
            })
        }
        composable(Screen.AddCoffee.route) {
            AddCoffeeScreen(navController = appState.navController)
        }
        composable(
            Screen.CoffeeDetails.route + "/{coffeeId}",
            arguments = listOf(navArgument("coffeeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val coffeeId = backStackEntry.arguments?.getInt("coffeeId")
            if (coffeeId != null) {
                CoffeeDetailsScreen(
                    navController = appState.navController,
                    coffeeId = coffeeId
                )
            }
        }
    }
}