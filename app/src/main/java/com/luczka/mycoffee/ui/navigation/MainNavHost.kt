package com.luczka.mycoffee.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.screens.brewassistant.BrewAssistantNavigationEvent
import com.luczka.mycoffee.ui.screens.brewassistant.BrewAssistantViewModel
import com.luczka.mycoffee.ui.screens.brewassistant.screens.BrewAssistantMainScreen
import com.luczka.mycoffee.ui.screens.brewdetails.BrewDetailsNavigationEvent
import com.luczka.mycoffee.ui.screens.brewdetails.BrewDetailsScreen
import com.luczka.mycoffee.ui.screens.brewdetails.BrewDetailsViewModel
import com.luczka.mycoffee.ui.screens.brewdetails.BrewDetailsViewModelFactory
import com.luczka.mycoffee.ui.screens.brewrating.AssistantRatingScreen
import com.luczka.mycoffee.ui.screens.coffeedetails.CoffeeDetailsNavigationEvent
import com.luczka.mycoffee.ui.screens.coffeedetails.CoffeeDetailsScreen
import com.luczka.mycoffee.ui.screens.coffeedetails.CoffeeDetailsViewModel
import com.luczka.mycoffee.ui.screens.coffeedetails.CoffeeDetailsViewModelFactory
import com.luczka.mycoffee.ui.screens.coffeeinput.CoffeeInputNavigationEvent
import com.luczka.mycoffee.ui.screens.coffeeinput.CoffeeInputScreen
import com.luczka.mycoffee.ui.screens.coffeeinput.CoffeeInputViewModel
import com.luczka.mycoffee.ui.screens.coffeeinput.CoffeeInputViewModelFactory
import com.luczka.mycoffee.ui.screens.equipmentinput.EquipmentInputAction
import com.luczka.mycoffee.ui.screens.equipmentinput.EquipmentInputScreen
import kotlinx.coroutines.launch

sealed class MainAction {
    data object OnMenuClicked : MainAction()
    data object NavigateToAssistant : MainAction()
    data class NavigateToBrewDetails(val brewId: Long) : MainAction()
    data class NavigateToCoffeeDetails(val coffeeId: Long) : MainAction()
    data class NavigateToCoffeeInput(val coffeeId: Long?) : MainAction()
    data class NavigateToEquipmentInput(val equipmentId: Long?) : MainAction()
}

val topLevelRoutes = listOf(
    TopLevelRoute(
        route = NestedNavHostRoutes.Home,
        stringRes = R.string.tab_home,
        drawableRes = R.drawable.ic_home_24_fill_0_weight_300_grade_0_opticalsize_24
    ),
    TopLevelRoute(
        route = NestedNavHostRoutes.Brews,
        stringRes = R.string.tab_history,
        drawableRes = R.drawable.ic_history_24_fill_0_weight_300_grade_0_opticalsize_24
    ),
    TopLevelRoute(
        route = NestedNavHostRoutes.Coffees,
        stringRes = R.string.tab_coffees,
        drawableRes = R.drawable.ic_browse_24_fill_0_weight_300_grade_0_opticalsize_24
    ),
//            TopLevelRoute(
//                route = Equipment,
//                stringRes = R.string.tab_equipment,
//                drawableRes = R.drawable.ic_coffee_maker_24_fill_0_weight_300_grade_0_opticalsize_24
//            ),
    TopLevelRoute(
        route = NestedNavHostRoutes.RecipeCategories,
        stringRes = R.string.tab_recipes,
        drawableRes = R.drawable.ic_list_alt_24_fill_0_weight_300_grade_0_opticalsize_24
    )
)

val drawerRoutes = listOf(
    TopLevelRoute(
        route = NestedNavHostRoutes.Home,
        stringRes = R.string.tab_home,
        drawableRes = R.drawable.ic_home_24_fill_0_weight_300_grade_0_opticalsize_24
    ),
    TopLevelRoute(
        route = NestedNavHostRoutes.Brews,
        stringRes = R.string.tab_history,
        drawableRes = R.drawable.ic_history_24_fill_0_weight_300_grade_0_opticalsize_24
    ),
    TopLevelRoute(
        route = NestedNavHostRoutes.Coffees,
        stringRes = R.string.tab_coffees,
        drawableRes = R.drawable.ic_browse_24_fill_0_weight_300_grade_0_opticalsize_24
    ),
//            TopLevelRoute(
//                route = Equipment,
//                stringRes = R.string.tab_equipment,
//                drawableRes = R.drawable.ic_coffee_maker_24_fill_0_weight_300_grade_0_opticalsize_24
//            ),
    TopLevelRoute(
        route = NestedNavHostRoutes.RecipeCategories,
        stringRes = R.string.tab_recipes,
        drawableRes = R.drawable.ic_list_alt_24_fill_0_weight_300_grade_0_opticalsize_24
    )
)

@Composable
fun MyCoffeeMainNavHost(
    widthSizeClass: WindowWidthSizeClass,
    mainNavController: NavHostController,
    nestedNavController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val mainNavBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val nestedNavBackStackEntry by nestedNavController.currentBackStackEntryAsState()

    val currentMainDestination = mainNavBackStackEntry?.destination
    val currentNestedDestination = nestedNavBackStackEntry?.destination

    val isMainRouteCurrentMainDestination = currentMainDestination?.hasRoute(MainNavHostRoute.Main::class) ?: false

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainRouteCurrentMainDestination,
        drawerContent = {
            ModalDrawerSheet(drawerTonalElevation = 0.dp) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    drawerRoutes.forEach { topLevelRoute ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = topLevelRoute.drawableRes),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = stringResource(id = topLevelRoute.stringRes))
                            },
                            selected = isMainRouteCurrentMainDestination && currentNestedDestination?.hierarchy?.any {
                                it.hasRoute(topLevelRoute.route::class)
                            } == true,
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                                nestedNavController.navigate(topLevelRoute.route) {
                                    popUpTo(nestedNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = mainNavController,
            startDestination = MainNavHostRoute.Main,
        ) {
            composable<MainNavHostRoute.Main> {
                MainRoute(
                    widthSizeClass = widthSizeClass,
                    nestedNavController = nestedNavController,
                    onAction = { action ->
                        when (action) {
                            MainAction.OnMenuClicked -> {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }

                            MainAction.NavigateToAssistant -> mainNavController.navigate(MainNavHostRoute.BrewAssistant)
                            is MainAction.NavigateToBrewDetails -> mainNavController.navigate(MainNavHostRoute.BrewDetails(action.brewId))
                            is MainAction.NavigateToCoffeeDetails -> mainNavController.navigate(MainNavHostRoute.CoffeeDetails(action.coffeeId))
                            is MainAction.NavigateToCoffeeInput -> mainNavController.navigate(MainNavHostRoute.CoffeeInput(action.coffeeId))
                            is MainAction.NavigateToEquipmentInput -> mainNavController.navigate(MainNavHostRoute.EquipmentInput(action.equipmentId))
                        }
                    }
                )
            }
            composable<MainNavHostRoute.BrewDetails>(
                enterTransition = fadeInAndSlideToStart(),
                exitTransition = fadeOutAndSlideToEnd()
            ) { backStackEntry ->
                val arguments = backStackEntry.toRoute<MainNavHostRoute.BrewDetails>()
                val viewModel = hiltViewModel<BrewDetailsViewModel, BrewDetailsViewModelFactory> { factory ->
                    factory.create(arguments.brewId)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { brewDetailsNavigationEvent ->
                        when (brewDetailsNavigationEvent) {
                            BrewDetailsNavigationEvent.NavigateUp -> mainNavController.navigateUp()
                        }
                    }
                }

                BrewDetailsScreen(
                    brewDetailsUiState = uiState,
                    onAction = viewModel::onAction
                )
            }
            composable<MainNavHostRoute.BrewAssistant>(
                enterTransition = fadeInAndSlideToStart(),
                exitTransition = fadeOutAndSlideToEnd()
            ) {
                val viewModel = hiltViewModel<BrewAssistantViewModel>()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { brewAssistantNavigationEvent ->
                        when (brewAssistantNavigationEvent) {
                            BrewAssistantNavigationEvent.NavigateUp -> mainNavController.navigateUp()
                            is BrewAssistantNavigationEvent.NavigateToBrewRating -> {
                                mainNavController.navigate(MainNavHostRoute.BrewRating(brewAssistantNavigationEvent.brewId)) {
                                    navOptions {
                                        popUpTo(MainNavHostRoute.BrewAssistant) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }

                BrewAssistantMainScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction
                )
            }
            composable<MainNavHostRoute.BrewRating>(
                enterTransition = fadeInAndSlideToStart(),
                exitTransition = fadeOutAndSlideToEnd()
            ) { backStackEntry ->
                val arguments = backStackEntry.toRoute<MainNavHostRoute.BrewRating>()
                AssistantRatingScreen()
            }
            composable<MainNavHostRoute.CoffeeDetails>(
                enterTransition = fadeInAndSlideToStart(),
                exitTransition = fadeOutAndSlideToEnd()
            ) { backStackEntry ->
                val arguments = backStackEntry.toRoute<MainNavHostRoute.CoffeeDetails>()
                val viewModel = hiltViewModel<CoffeeDetailsViewModel, CoffeeDetailsViewModelFactory> { factory ->
                    factory.create(arguments.coffeeId)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { event ->
                        when (event) {
                            CoffeeDetailsNavigationEvent.NavigateUp -> mainNavController.navigateUp()
                            is CoffeeDetailsNavigationEvent.NavigateToCoffeeInput -> mainNavController.navigate(MainNavHostRoute.CoffeeInput(event.coffeeId))
                        }
                    }
                }

                CoffeeDetailsScreen(
                    widthSizeClass = widthSizeClass,
                    uiState = uiState,
                    onAction = viewModel::onAction
                )
            }
            composable<MainNavHostRoute.CoffeeInput>(
                enterTransition = fadeInAndSlideToStart(),
                exitTransition = fadeOutAndSlideToEnd()
            ) { backStackEntry ->
                val arguments = backStackEntry.toRoute<MainNavHostRoute.CoffeeInput>()
                val viewModel = hiltViewModel<CoffeeInputViewModel, CoffeeInputViewModelFactory> { factory ->
                    factory.create(arguments.coffeeId)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { event ->
                        when (event) {
                            CoffeeInputNavigationEvent.NavigateUp -> mainNavController.navigateUp()
                        }
                    }
                }

                CoffeeInputScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction
                )
            }
            composable<MainNavHostRoute.EquipmentInput>(
                enterTransition = fadeInAndSlideToStart(),
                exitTransition = fadeOutAndSlideToEnd()
            ) {
                EquipmentInputScreen(
                    onAction = { action ->
                        when (action) {
                            EquipmentInputAction.NavigateUp -> mainNavController.navigateUp()
                        }
                    }
                )
            }
        }
    }
}

private fun fadeInAndSlideToStart(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards EnterTransition? = {
    fadeIn(
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    ) + slideIntoContainer(
        animationSpec = tween(durationMillis = 300, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Start
    )
}


private fun fadeOutAndSlideToEnd(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards ExitTransition? = {
    fadeOut(
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    ) + slideOutOfContainer(
        animationSpec = tween(durationMillis = 300, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.End
    )
}