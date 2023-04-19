package com.coffee.mycoffeeassistant.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coffee.mycoffeeassistant.ui.MyCoffeeAssistantAppState
import com.coffee.mycoffeeassistant.ui.components.UserGreeting
import com.coffee.mycoffeeassistant.ui.screens.HomeScreen

fun NavGraphBuilder.homeNavGraph(appState: MyCoffeeAssistantAppState) {
    navigation(
        startDestination = Screen.Home.route,
        route = "home"
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = appState.navController)
        }
        composable(Screen.BrewAssistant.route) {
            UserGreeting("Assistant")
        }
    }
}