package com.luczka.mycoffee.data.mappers

import androidx.core.net.toUri
import com.luczka.mycoffee.data.database.entities.CoffeeImageEntity
import com.luczka.mycoffee.domain.models.CoffeeImageModel

fun CoffeeImageModel.toEntity() : CoffeeImageEntity {
    return CoffeeImageEntity(
        coffeeImageId = coffeeImageId,
        uri = uri.toString(),
        filename = filename,
        index = index
    )
}

fun CoffeeImageEntity.toModel() : CoffeeImageModel {
    return CoffeeImageModel(
        coffeeImageId = coffeeImageId,
        uri = uri.toUri(),
        filename = filename,
        index = index
    )
}

fun List<CoffeeImageEntity>.toModel() : List<CoffeeImageModel> {
    return this.map { it.toModel() }
}