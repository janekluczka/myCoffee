package com.luczka.mycoffee.ui.mappers

import com.luczka.mycoffee.domain.models.BrewedCoffeeModel
import com.luczka.mycoffee.ui.models.BrewedCoffeeUiState

fun BrewedCoffeeModel.toUiState(): BrewedCoffeeUiState {
    return BrewedCoffeeUiState(
        coffeeAmount = coffeeAmount,
        coffee = coffee.toUiState()
    )
}

fun List<BrewedCoffeeModel>.toUiState(): List<BrewedCoffeeUiState> {
    return this.map { it.toUiState() }
}

fun BrewedCoffeeUiState.toModel(): BrewedCoffeeModel {
    return BrewedCoffeeModel(
        coffeeAmount = coffeeAmount,
        coffee = coffee.toModel()
    )
}

fun List<BrewedCoffeeUiState>.toModel(): List<BrewedCoffeeModel> {
    return this.map { it.toModel() }
}