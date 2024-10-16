package com.luczka.mycoffee.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luczka.mycoffee.domain.models.Process
import com.luczka.mycoffee.domain.models.Roast
import com.luczka.mycoffee.ui.models.CoffeeUiState
import com.luczka.mycoffee.util.toStringWithOneDecimalPoint
import com.luczka.mycoffee.util.toStringWithTwoDecimalPoints

@Entity
data class CoffeeEntity(
    @PrimaryKey(autoGenerate = true)
    val coffeeId: Int,
    val name: String,
    val brand: String,
    val amount: Float?,
    val scaScore: Float?,
    val roast: Int?,
    val process: Int?,
    val isFavourite: Boolean,
    val imageFile240x240: String?,
    val imageFile360x360: String?,
    val imageFile480x480: String?,
    val imageFile720x720: String?,
    val imageFile960x960: String?,
) : Comparable<CoffeeEntity> {

    override fun compareTo(other: CoffeeEntity): Int {
        return compareBy<CoffeeEntity>(
            { it.isFavourite },
            { it.name },
            { it.brand },
            { it.amount },
            { it.coffeeId }
        ).compare(this, other)
    }

    fun toCoffeeUiState(): CoffeeUiState = CoffeeUiState(
        coffeeId = coffeeId,
        name = name,
        brand = brand,
        amount = amount?.toStringWithOneDecimalPoint(),
        scaScore = scaScore?.toStringWithTwoDecimalPoints(),
        process = Process.values().firstOrNull { it.id == process },
        roast = Roast.values().firstOrNull { it.id == roast },
        isFavourite = isFavourite,
        imageFile240x240 = imageFile240x240,
        imageFile360x360 = imageFile360x360,
        imageFile480x480 = imageFile480x480,
        imageFile720x720 = imageFile720x720,
        imageFile960x960 = imageFile960x960,
    )

}