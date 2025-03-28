package com.luczka.mycoffee.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.mycoffee.domain.usecases.GetRecentlyAddedBrewsFlowUseCase
import com.luczka.mycoffee.domain.usecases.GetRecentlyAddedCoffeesFlowUseCase
import com.luczka.mycoffee.ui.mappers.toUiState
import com.luczka.mycoffee.ui.models.BrewUiState
import com.luczka.mycoffee.ui.models.CoffeeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class HomeViewModelState(
    val recentBrews: List<BrewUiState> = emptyList(),
    val recentlyAddedCoffees: List<CoffeeUiState> = emptyList(),
) {
    fun toHomeUiState(): HomeUiState {
        return HomeUiState.HasCoffees(
            recentBrews = recentBrews,
            recentlyAddedCoffees = recentlyAddedCoffees
        )
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentlyAddedCoffeesFlowUseCase: GetRecentlyAddedCoffeesFlowUseCase,
    private val getRecentlyAddedBrewsFlowUseCase: GetRecentlyAddedBrewsFlowUseCase
) : ViewModel() {

    private val _viewModelState = MutableStateFlow(HomeViewModelState())
    val uiState = _viewModelState
        .map(HomeViewModelState::toHomeUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value.toHomeUiState()
        )

    private val _oneTimeEvent = MutableSharedFlow<HomeOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            getRecentlyAddedCoffeesFlowUseCase(amount = 5).collect { coffeeModels ->
                _viewModelState.update {
                    it.copy(recentlyAddedCoffees = coffeeModels.toUiState())
                }
            }
        }
        viewModelScope.launch {
            getRecentlyAddedBrewsFlowUseCase(amount = 5).collect { brewModels ->
                _viewModelState.update {
                    it.copy(recentBrews = brewModels.toUiState())
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnMenuClicked -> {}
            HomeAction.NavigateToBrewAssistant -> navigateToAssistant()
            is HomeAction.NavigateToBrewDetails -> navigateToBrewDetails(action.brewId)
            is HomeAction.NavigateToCoffeeDetails -> navigateToCoffeeDetails(action.coffeeId)
        }
    }

    private fun navigateToAssistant() {
        viewModelScope.launch {
            _oneTimeEvent.emit(HomeOneTimeEvent.NavigateToBrewAssistant)
        }
    }

    private fun navigateToBrewDetails(brewId: Long) {
        viewModelScope.launch {
            _oneTimeEvent.emit(HomeOneTimeEvent.NavigateToBrewDetails(brewId))
        }
    }

    private fun navigateToCoffeeDetails(coffeeId: Long) {
        viewModelScope.launch {
            _oneTimeEvent.emit(HomeOneTimeEvent.NavigateToCoffeeDetails(coffeeId))
        }
    }
}