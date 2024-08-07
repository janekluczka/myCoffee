package com.luczka.mycoffee.ui.screen.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luczka.mycoffee.data.database.entities.Brew
import com.luczka.mycoffee.data.database.entities.BrewedCoffee
import com.luczka.mycoffee.data.repository.MyCoffeeDatabaseRepository
import com.luczka.mycoffee.ui.model.CoffeeUiState
import com.luczka.mycoffee.util.toStringWithOneDecimalPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed interface BrewAssistantUiState {
    val currentCoffees: List<CoffeeUiState>
    val selectedAmountsSum: String
    val waterAmount: String
    val ratioSelectionUiState: RatioSelectionUiState
    val rating: Int?
    val notes: String
    val isFinished: Boolean

    data class NoneSelected(
        override val currentCoffees: List<CoffeeUiState> = emptyList(),
        val amountSelectionUiState: AmountSelectionUiState = AmountSelectionUiState(),
        override val selectedAmountsSum: String = "0.0",
        override val ratioSelectionUiState: RatioSelectionUiState = RatioSelectionUiState(),
        override val waterAmount: String = "0.0",
        override val rating: Int? = null,
        override val notes: String = "",
        override val isFinished: Boolean = false,
    ) : BrewAssistantUiState

    data class CoffeeSelected(
        override val currentCoffees: List<CoffeeUiState> = emptyList(),
        val selectedCoffees: Map<CoffeeUiState, AmountSelectionUiState> = emptyMap(),
        override val selectedAmountsSum: String = "0.0",
        override val ratioSelectionUiState: RatioSelectionUiState = RatioSelectionUiState(),
        override val waterAmount: String = "0.0",
        override val rating: Int? = null,
        override val notes: String = "",
        override val isFinished: Boolean = false
    ) : BrewAssistantUiState
}

data class AmountSelectionUiState(
    val integerParts: List<Int> = (0..0).toList(),
    val decimalParts: List<Int> = (0..9).toList(),
    val integerPartIndex: Int = 0,
    val decimalPartIndex: Int = 0,
    val selectedAmount: String = "0.0"
)

data class RatioSelectionUiState(
    val coffeeRatios: List<Int> = (1..10).toList(),
    val waterRatios: List<Int> = (1..100).toList(),
    val coffeeRatioIndex: Int = 0,
    val waterRatioIndex: Int = 0,
    val selectedCoffeeRatio: Int = 1,
    val selectedWaterRatio: Int = 1
)

private data class AssistantViewModelState(
    val currentCoffees: List<CoffeeUiState> = emptyList(),
    val selectedCoffees: MutableMap<CoffeeUiState, AmountSelectionUiState> = mutableMapOf(),
    val amountSelectionUiState: AmountSelectionUiState = AmountSelectionUiState(integerParts = (0..99).toList()),
    val ratioSelectionUiState: RatioSelectionUiState = RatioSelectionUiState(),
    val rating: Int? = null,
    val notes: String = "",
    val isFinished: Boolean = false,
) {
    fun toAssistantUiState(): BrewAssistantUiState {
        val selectedAmountsSum = sumSelectedAmounts()

        val selectedWaterRatio = ratioSelectionUiState.selectedWaterRatio
        val selectedCoffeeRatio = ratioSelectionUiState.selectedCoffeeRatio

        val waterAmount = selectedAmountsSum * selectedWaterRatio / selectedCoffeeRatio

        val selectedAmountsSumFormatted = selectedAmountsSum.toStringWithOneDecimalPoint()
        val waterAmountFormatted = waterAmount.toStringWithOneDecimalPoint()

        return if (selectedCoffees.isEmpty()) {
            BrewAssistantUiState.NoneSelected(
                currentCoffees = currentCoffees,
                isFinished = isFinished,
                selectedAmountsSum = selectedAmountsSumFormatted,
                waterAmount = waterAmountFormatted,
                amountSelectionUiState = amountSelectionUiState,
                ratioSelectionUiState = ratioSelectionUiState,
                rating = rating,
                notes = notes
            )
        } else {
            BrewAssistantUiState.CoffeeSelected(
                currentCoffees = currentCoffees,
                selectedCoffees = selectedCoffees,
                isFinished = isFinished,
                selectedAmountsSum = selectedAmountsSumFormatted,
                waterAmount = waterAmountFormatted,
                ratioSelectionUiState = ratioSelectionUiState,
                rating = rating,
                notes = notes
            )
        }
    }

    fun toBrew(): Brew {
        val coffeeAmountsSum = sumSelectedAmounts()

        val coffeeRatio = ratioSelectionUiState.selectedCoffeeRatio
        val waterRatio = ratioSelectionUiState.selectedWaterRatio

        val waterAmount = coffeeAmountsSum * waterRatio / coffeeRatio

        return Brew(
            brewId = 0,
            date = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE),
            coffeeAmount = coffeeAmountsSum,
            coffeeRatio = coffeeRatio,
            waterAmount = waterAmount,
            waterRatio = waterRatio,
            rating = rating,
            notes = notes
        )
    }

    private fun sumSelectedAmounts(): Float {
        return if (selectedCoffees.isEmpty()) {
            amountSelectionUiState.selectedAmount.toFloatOrNull() ?: 0f
        } else {
            selectedCoffees.values.map { it.selectedAmount.toFloatOrNull() ?: 0f }.sum()
        }
    }
}

class AssistantViewModel(
    private val myCoffeeDatabaseRepository: MyCoffeeDatabaseRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(AssistantViewModelState())
    val uiState = viewModelState
        .map(AssistantViewModelState::toAssistantUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toAssistantUiState()
        )

    init {
        viewModelScope.launch {
            myCoffeeDatabaseRepository.getCurrentCoffeesStream().collect { coffeeList ->
                val coffeeUiStateList = coffeeList
                    .map { it.toCoffeeUiState() }
                    .sortedWith(
                        compareBy<CoffeeUiState> { !it.isFavourite }
                            .thenBy { it.name }
                            .thenBy { it.brand }
                            .thenBy { it.amount }
                    )
                viewModelState.update { it.copy(currentCoffees = coffeeUiStateList) }
            }
        }
    }

    fun selectCoffee(coffeeUiState: CoffeeUiState) {
        val selectedCoffeeAmount = coffeeUiState.amount?.toFloatOrNull()?.toInt()

        val lastIntegerPart = when {
            selectedCoffeeAmount == null -> 0
            selectedCoffeeAmount > 99 -> 99
            else -> selectedCoffeeAmount
        }

        val updatedSelectedCoffees = viewModelState.value.selectedCoffees.toMutableMap()

        if (updatedSelectedCoffees.containsKey(coffeeUiState)) {
            updatedSelectedCoffees.remove(coffeeUiState)
        } else {
            updatedSelectedCoffees[coffeeUiState] = AmountSelectionUiState(
                integerParts = (0..lastIntegerPart).toList()
            )
        }

        viewModelState.update { it.copy(selectedCoffees = updatedSelectedCoffees) }
    }

    fun updateAmountSelectionIntegerPart(integerPartIndex: Int) {
        val amountSelectionUiState = viewModelState.value.amountSelectionUiState

        val decimalPartIndex = amountSelectionUiState.decimalPartIndex

        val integerPart = amountSelectionUiState.integerParts[integerPartIndex]
        val decimalPart = amountSelectionUiState.decimalParts[decimalPartIndex]

        val updatedAmountSelectionUiState = amountSelectionUiState.copy(
            integerPartIndex = integerPartIndex,
            selectedAmount = "$integerPart.$decimalPart"
        )

        viewModelState.update { it.copy(amountSelectionUiState = updatedAmountSelectionUiState) }
    }

    fun updateAmountSelectionDecimalPart(decimalPartIndex: Int) {
        val amountSelectionUiState = viewModelState.value.amountSelectionUiState

        val integerPartIndex = amountSelectionUiState.integerPartIndex

        val integerPart = amountSelectionUiState.integerParts[integerPartIndex]
        val decimalPart = amountSelectionUiState.decimalParts[decimalPartIndex]

        val updatedAmountSelectionUiState = amountSelectionUiState.copy(
            decimalPartIndex = decimalPartIndex,
            selectedAmount = "$integerPart.$decimalPart"
        )

        viewModelState.update { it.copy(amountSelectionUiState = updatedAmountSelectionUiState) }
    }

    fun updateAmountSelectionIntegerPart(key: CoffeeUiState, integerPartIndex: Int) {
        val selectedCoffees = viewModelState.value.selectedCoffees.toMutableMap()

        val amountSelectionUiState = selectedCoffees[key] ?: return

        val decimalPartIndex = amountSelectionUiState.decimalPartIndex

        val integerPart = amountSelectionUiState.integerParts[integerPartIndex]
        val decimalPart = amountSelectionUiState.decimalParts[decimalPartIndex]

        val maxAmount = key.amount ?: return
        val selectedAmount = "$integerPart.$decimalPart"

        val maxAmountFloat = maxAmount.toFloatOrNull() ?: return
        val selectedAmountFloat = selectedAmount.toFloatOrNull() ?: return

        val updatedAmountSelectionUiState = if (selectedAmountFloat > maxAmountFloat) {
            val selectedAmountDecimalPart = selectedAmount.split(".")[1].toIntOrNull() ?: return
            val maxAmountDecimalPart = maxAmount.split(".")[1].toIntOrNull() ?: return

            val decimalPartIndexOffset = selectedAmountDecimalPart - maxAmountDecimalPart

            val updatedDecimalPartIndex = decimalPartIndex - decimalPartIndexOffset

            val updatedDecimalPart = amountSelectionUiState.decimalParts[updatedDecimalPartIndex]

            amountSelectionUiState.copy(
                integerPartIndex = integerPartIndex,
                decimalPartIndex = updatedDecimalPartIndex,
                selectedAmount = "$integerPart.$updatedDecimalPart",
            )
        } else {
            amountSelectionUiState.copy(
                integerPartIndex = integerPartIndex,
                selectedAmount = "$integerPart.$decimalPart"
            )
        }

        selectedCoffees.replace(key, updatedAmountSelectionUiState)

        viewModelState.update { it.copy(selectedCoffees = selectedCoffees) }
    }

    fun updateAmountSelectionDecimalPart(key: CoffeeUiState, decimalPartIndex: Int) {
        val selectedCoffees = viewModelState.value.selectedCoffees.toMutableMap()

        val amountSelectionUiState = selectedCoffees[key] ?: return

        val integerPartIndex = amountSelectionUiState.integerPartIndex

        val integerPart = amountSelectionUiState.integerParts[integerPartIndex]
        val decimalPart = amountSelectionUiState.decimalParts[decimalPartIndex]

        val updatedAmountSelectionUiState = amountSelectionUiState.copy(
            decimalPartIndex = decimalPartIndex,
            selectedAmount = "$integerPart.$decimalPart"
        )

        selectedCoffees.replace(key, updatedAmountSelectionUiState)

        viewModelState.update { it.copy(selectedCoffees = selectedCoffees) }
    }

    fun updateAmountSelectionValue(coffeeAmountValue: String) {
        val amountSelectionUiState = viewModelState.value.amountSelectionUiState

        val coffeeAmountFloat = coffeeAmountValue.toFloatOrNull() ?: return

        val coffeeAmountValueWithDecimalPoint = coffeeAmountFloat.toStringWithOneDecimalPoint()

        val coffeeAmountSplit = coffeeAmountValueWithDecimalPoint.split(".")

        val integerPart = coffeeAmountSplit[0].toIntOrNull() ?: return
        val decimalPart = coffeeAmountSplit[1].toIntOrNull() ?: return

        val updatedAmountSelectionUiState = amountSelectionUiState.copy(
            selectedAmount = coffeeAmountValue,
            integerPartIndex = amountSelectionUiState.integerParts.indexOf(integerPart),
            decimalPartIndex = amountSelectionUiState.decimalParts.indexOf(decimalPart)
        )

        viewModelState.update { it.copy(amountSelectionUiState = updatedAmountSelectionUiState) }
    }

    fun updateAmountSelectionValue(key: CoffeeUiState, coffeeAmountValue: String) {
        val selectedCoffees = viewModelState.value.selectedCoffees

        val amountSelectionUiState = selectedCoffees[key] ?: return

        val coffeeAmountFloat = coffeeAmountValue.toFloatOrNull() ?: return

        val coffeeAmountValueWithDecimalPoint = coffeeAmountFloat.toStringWithOneDecimalPoint()

        val coffeeAmountSplit = coffeeAmountValueWithDecimalPoint.split(".")

        val integerPart = coffeeAmountSplit[0].toIntOrNull() ?: return
        val decimalPart = coffeeAmountSplit[1].toIntOrNull() ?: return

        val updatedAmountSelectionUiState = amountSelectionUiState.copy(
            selectedAmount = coffeeAmountValue,
            integerPartIndex = amountSelectionUiState.integerParts.indexOf(integerPart),
            decimalPartIndex = amountSelectionUiState.decimalParts.indexOf(decimalPart)
        )

        selectedCoffees.replace(key, updatedAmountSelectionUiState)

        viewModelState.update { it.copy(selectedCoffees = selectedCoffees) }
    }

    fun updateCoffeeRatioIndex(coffeeRatioIndex: Int) {
        val ratioSelectionUiState = viewModelState.value.ratioSelectionUiState

        val updatedRatioSelectionUiState = ratioSelectionUiState.copy(
            coffeeRatioIndex = coffeeRatioIndex,
            selectedCoffeeRatio = ratioSelectionUiState.coffeeRatios[coffeeRatioIndex],
        )

        viewModelState.update { it.copy(ratioSelectionUiState = updatedRatioSelectionUiState) }
    }

    fun updateWaterRatioIndex(waterRatioIndex: Int) {
        val ratioSelectionUiState = viewModelState.value.ratioSelectionUiState

        val updatedRatioSelectionUiState = ratioSelectionUiState.copy(
            waterRatioIndex = waterRatioIndex,
            selectedWaterRatio = ratioSelectionUiState.waterRatios[waterRatioIndex],
        )

        viewModelState.update { it.copy(ratioSelectionUiState = updatedRatioSelectionUiState) }
    }

    fun updateRatioValues(coffeeRatioValue: String, waterRatioValue: String) {
        val ratioSelectionUiState = viewModelState.value.ratioSelectionUiState

        val selectedCoffeeRatio = if (coffeeRatioValue.isNotBlank()) {
            coffeeRatioValue.toIntOrNull() ?: return
        } else {
            ratioSelectionUiState.selectedCoffeeRatio
        }

        val selectedWaterRatio = if (waterRatioValue.isNotBlank()) {
            waterRatioValue.toIntOrNull() ?: return
        } else {
            ratioSelectionUiState.selectedWaterRatio
        }

        val updatedRatioSelectionUiState = ratioSelectionUiState.copy(
            selectedCoffeeRatio = selectedCoffeeRatio,
            selectedWaterRatio = selectedWaterRatio,
            coffeeRatioIndex = ratioSelectionUiState.coffeeRatios.indexOf(selectedCoffeeRatio),
            waterRatioIndex = ratioSelectionUiState.waterRatios.indexOf(selectedWaterRatio)
        )

        viewModelState.update { it.copy(ratioSelectionUiState = updatedRatioSelectionUiState) }
    }

    fun updateRating(rating: Int?) {
        viewModelState.update { it.copy(rating = rating) }
    }

    fun updateNotes(notes: String) {
        viewModelState.update { it.copy(notes = notes) }
    }

    fun finishBrew() {
        viewModelScope.launch {
            val brewId = insertBrew()
            updateSelectedCoffees(brewId = brewId)
            viewModelState.update { it.copy(isFinished = true) }
        }
    }

    private suspend fun insertBrew(): Long {
        val brew = viewModelState.value.toBrew()
        return myCoffeeDatabaseRepository.insertBrew(brew = brew)
    }

    private suspend fun updateSelectedCoffees(brewId: Long) {
        viewModelState.value.selectedCoffees.forEach { (selectedCoffee, amountSelectionUiState) ->
            val selectedCoffeeAmount = selectedCoffee.amount?.toFloatOrNull() ?: 0f

            val selectedAmount = amountSelectionUiState.selectedAmount.toFloatOrNull() ?: 0f

            val updatedAmount = selectedCoffeeAmount - selectedAmount

            val updatedCoffee = if (updatedAmount <= 0) {
                selectedCoffee.copy(amount = null)
            } else {
                selectedCoffee.copy(amount = updatedAmount.toString())
            }

            val brewedCoffee = BrewedCoffee(
                brewId = brewId.toInt(),
                coffeeId = selectedCoffee.coffeeId,
                coffeeAmount = selectedAmount
            )

            myCoffeeDatabaseRepository.insertBrewedCoffee(brewedCoffee)

            myCoffeeDatabaseRepository.updateCoffee(updatedCoffee.toCoffee())
        }
    }

}