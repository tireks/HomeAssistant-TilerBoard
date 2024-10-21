package com.tirexmurina.tilerboard.shared.tile.util

sealed interface TileType {

    data object TemperatureSimple : TileType

    data object HumiditySimple : TileType

    data object BinarySimple : TileType

}