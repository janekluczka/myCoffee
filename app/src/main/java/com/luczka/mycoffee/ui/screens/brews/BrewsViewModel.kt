package com.luczka.mycoffee.ui.screens.brews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.mycoffee.domain.repositories.MyCoffeeDatabaseRepository
import com.luczka.mycoffee.ui.mappers.toModel
import com.luczka.mycoffee.ui.mappers.toUiState
import com.luczka.mycoffee.ui.models.BrewUiState
import com.luczka.mycoffee.ui.models.SwipeableListItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class BrewsViewModelState(
    val selectedFilter: BrewFilterUiState = BrewFilterUiState.LATEST,
    val brews: List<SwipeableListItemUiState<BrewUiState>> = emptyList()
) {
    fun toBrewsUiState(): BrewsUiState {
        return if (brews.isEmpty()) {
            BrewsUiState.NoBrews
        } else {
            BrewsUiState.HasBrews(
                selectedFilter = selectedFilter,
                brews = filterBrews()
            )
        }
    }

    private fun filterBrews(): List<SwipeableListItemUiState<BrewUiState>> {
        return when (selectedFilter) {
            BrewFilterUiState.LATEST -> {
                brews
            }

            BrewFilterUiState.BEST_RATED -> {
                brews.sortedByDescending { it.item.rating }
            }
        }
    }
}

@HiltViewModel
class BrewsViewModel @Inject constructor(
    private val myCoffeeDatabaseRepository: MyCoffeeDatabaseRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(BrewsViewModelState())
    val uiState = viewModelState
        .map(BrewsViewModelState::toBrewsUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toBrewsUiState()
        )

    init {
        viewModelScope.launch {
            myCoffeeDatabaseRepository
                .getAllBrewsFlow()
                .map { brewModels ->
                    brewModels.map { brewModel ->
                        SwipeableListItemUiState(item = brewModel.toUiState())
                    }
                }
                .collect { swipeableListItemUiStates ->
                    viewModelState.update {
                        it.copy(brews = swipeableListItemUiStates)
                    }
                }
        }
    }

    fun onAction(action: BrewsAction) {
        when (action) {
            is BrewsAction.NavigateToAssistant -> {}
            is BrewsAction.NavigateToBrewDetails -> {}

            is BrewsAction.OnSelectedFilterChanged -> selectFilter(action.brewFilter)
            is BrewsAction.OnItemActionsExpanded -> collapseOtherItemsActions(action.brewId)
            is BrewsAction.OnItemActionsCollapsed -> collapseItemsActions(action.brewId)
            is BrewsAction.OnDeleteClicked -> deleteBrew(action.brewId)
        }
    }

    private fun selectFilter(brewFilter: BrewFilterUiState) {
        viewModelState.update {
            it.copy(selectedFilter = brewFilter)
        }
    }

    private fun collapseOtherItemsActions(expandedBrewId: Long?) {
        viewModelState.update { currentState ->
            currentState.copy(
                brews = currentState.brews.map { itemState ->
                    when {
                        itemState.item.brewId == expandedBrewId -> itemState.copy(isRevealed = true)
                        itemState.isRevealed -> itemState.copy(isRevealed = false)
                        else -> itemState
                    }
                }
            )
        }
    }

    private fun collapseItemsActions(collapsedBrewId: Long) {
        viewModelState.update { currentState ->
            currentState.copy(
                brews = currentState.brews.map { itemState ->
                    if (itemState.item.brewId == collapsedBrewId) {
                        itemState.copy(isRevealed = false)
                    } else {
                        itemState
                    }
                }
            )
        }
    }

    private fun deleteBrew(brewId: Long) {
        val swipeableListItemUiState = viewModelState.value.brews.find { it.item.brewId == brewId }
        val brewUiState = swipeableListItemUiState?.item ?: return
        viewModelScope.launch {
            myCoffeeDatabaseRepository.deleteBrew(brewUiState.toModel())
        }
    }

}