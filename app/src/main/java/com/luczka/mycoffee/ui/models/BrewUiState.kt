package com.luczka.mycoffee.ui.models

import com.luczka.mycoffee.data.database.entities.BrewEntity
import java.time.LocalDate

data class BrewUiState(
    val brewId: Int,
    val date: LocalDate,
    val coffeeAmount: Float,
    val coffeeRatio: Int,
    val waterAmount: Float,
    val waterRatio: Int,
    val rating: Int?,
    val notes: String,
    val brewedCoffees: List<BrewedCoffeeUiState>
) : Comparable<BrewUiState> {
    override fun compareTo(other: BrewUiState): Int {
        return compareBy<BrewUiState>(
            { it.brewId },
            { it.date }
        ).compare(this, other)
    }

    fun toBrew(): BrewEntity = BrewEntity(
        brewId = brewId,
        date = "",
        coffeeAmount = coffeeAmount,
        coffeeRatio = coffeeRatio,
        waterAmount = waterAmount,
        waterRatio = waterRatio,
        rating = rating,
        notes = notes,
    )
}