package com.luczka.mycoffee.model

import androidx.annotation.Keep
import com.luczka.mycoffee.ui.model.StepUiState

@Keep
data class Step(
    val description: String = "",
    val time: String? = null,
) {
    fun toStepUiState(index: Int): StepUiState = StepUiState(
        number = index + 1,
        description = description,
        time = time
    )
}