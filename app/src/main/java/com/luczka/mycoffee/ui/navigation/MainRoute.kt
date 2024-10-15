package com.luczka.mycoffee.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainRoute(
    widthSizeClass: WindowWidthSizeClass,
    nestedNavController: NavHostController,
    navigateToAssistant: () -> Unit,
    navigateToCoffeeInput: (Int?) -> Unit
) {
    val navigationType = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> MyCoffeeNavigationType.BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium -> MyCoffeeNavigationType.NAVIGATION_RAIL
        WindowWidthSizeClass.Expanded -> MyCoffeeNavigationType.NAVIGATION_RAIL
        else -> MyCoffeeNavigationType.BOTTOM_NAVIGATION
    }

    when (navigationType) {
        MyCoffeeNavigationType.BOTTOM_NAVIGATION -> {
            Scaffold(
                bottomBar = { MyCoffeeNavigationBar(navController = nestedNavController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    MyCoffeeNestedNavHost(
                        modifier = Modifier.weight(1f),
                        widthSizeClass = widthSizeClass,
                        navController = nestedNavController,
                        navigateToAssistant = navigateToAssistant,
                        navigateToCoffeeInput = navigateToCoffeeInput
                    )
                    Divider()
                }
            }
        }

        MyCoffeeNavigationType.NAVIGATION_RAIL -> {
            Row(modifier = Modifier.fillMaxSize()) {
                MyCoffeeNavigationRail(navController = nestedNavController)
                MyCoffeeNestedNavHost(
                    modifier = Modifier,
                    widthSizeClass = widthSizeClass,
                    navController = nestedNavController,
                    navigateToAssistant = navigateToAssistant,
                    navigateToCoffeeInput = navigateToCoffeeInput
                )
            }
        }
    }
}

@Composable
private fun MyCoffeeNavigationRail(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        Spacer(modifier = Modifier.weight(1f))
        Routes.Main.topLevelRoutes.forEach { topLevelRoute ->
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(id = topLevelRoute.drawableRes),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = topLevelRoute.stringRes)) },
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(topLevelRoute.route::class)
                } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MyCoffeeNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(tonalElevation = 0.dp) {
        Routes.Main.topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(topLevelRoute.drawableRes),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = topLevelRoute.stringRes)) },
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(topLevelRoute.route::class)
                } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}