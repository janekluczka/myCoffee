package com.luczka.mycoffee.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.mycoffee.domain.repositories.MyCoffeeDatabaseRepository
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
    private val myCoffeeDatabaseRepository: MyCoffeeDatabaseRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState())
    val uiState = viewModelState
        .map(HomeViewModelState::toHomeUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toHomeUiState()
        )

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            myCoffeeDatabaseRepository.getRecentlyAddedCoffeesFlow(amount = 5).collect { coffeeModels ->
                val recentlyAddedCoffees = coffeeModels.map { it.toUiState() }
                viewModelState.update {
                    it.copy(recentlyAddedCoffees = recentlyAddedCoffees)
                }
            }
        }
        viewModelScope.launch {
            myCoffeeDatabaseRepository.getRecentBrewsFlow(amount = 5).collect { brewModels ->
                val recentBrews = brewModels.map { it.toUiState() }
                viewModelState.update {
                    it.copy(recentBrews = recentBrews)
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.OnMenuClicked -> {}
            is HomeAction.NavigateToBrewAssistant -> navigateToAssistant()
            is HomeAction.NavigateToBrewDetails -> navigateToBrewDetails(action.brewId)
            is HomeAction.NavigateToCoffeeDetails -> navigateToCoffeeDetails(action.coffeeId)
        }
    }

    private fun navigateToAssistant() {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToBrewAssistant)
        }
    }

    private fun navigateToBrewDetails(brewId: Long) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToBrewDetails(brewId))
        }
    }

    private fun navigateToCoffeeDetails(coffeeId: Long) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToCoffeeDetails(coffeeId))
        }
    }

}