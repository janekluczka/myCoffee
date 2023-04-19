package com.coffee.mycoffeeassistant.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.coffee.mycoffeeassistant.ui.MyCoffeeAssistantAppState
import com.coffee.mycoffeeassistant.ui.components.UserGreeting
import com.coffee.mycoffeeassistant.ui.screens.CupboardScreen

fun NavGraphBuilder.cupboardNavGraph(appState: MyCoffeeAssistantAppState) {
    navigation(
        startDestination = Screen.Cupboard.route,
        route = "cupboard"
    ) {
        composable(Screen.Cupboard.route) {
            CupboardScreen(appState.navController)
        }
        composable(Screen.AddCoffee.route) {
            UserGreeting("Add")
        }
        composable(
            Screen.CoffeeDetails.route + "/{coffeeId}",
            arguments = listOf(navArgument("coffeeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val coffeeId = backStackEntry.arguments?.getInt("coffeeId")
            UserGreeting("My coffee beans $coffeeId")
        }
    }
}