package com.luczka.mycoffee.enums

import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.model.DropdownMenuItemUiState

/**
 * Enum class representing different coffee processing methods.
 *
 * @property id The unique identifier for the processing method. This value should not be changed as it is used for database storage.
 * @property stringResource The resource ID for the string representation of the processing method.
 */
enum class Process(
    val id: Int,
    val stringResource: Int
) {
    Natural(1, R.string.process_natural),
    Washed(2, R.string.process_washed),
    Honey(3, R.string.process_honey)
}

fun Process.toDropdownMenuUiState(): DropdownMenuItemUiState<Process> =
    DropdownMenuItemUiState(
        item = this,
        stringResource = stringResource
    )