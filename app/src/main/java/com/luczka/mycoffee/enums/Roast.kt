package com.luczka.mycoffee.enums

import com.luczka.mycoffee.R
import com.luczka.mycoffee.ui.model.DropdownMenuItemUiState

/**
 * Enum class representing different coffee roast levels.
 *
 * @property id The unique identifier for the roast level. This value should not be changed as it is used for database storage.
 * @property stringResource The resource ID for the string representation of the roast level.
 */
enum class Roast(val id: Int, val stringResource: Int) {
    Light(1, R.string.roast_light),
    Medium(2, R.string.roast_medium),
    MediumDark(3, R.string.roast_medium_dark),
    Dark(4, R.string.roast_dark)
}

fun Roast.toDropdownMenuUiState(): DropdownMenuItemUiState<Roast> =
    DropdownMenuItemUiState(
        item = this,
        stringResource = stringResource
    )