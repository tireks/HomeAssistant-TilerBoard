package com.tirexmurina.tilerboard.shared.tile.util

/**
 * добавил сюда - добавь и в TileTypeEnum
 */
sealed interface TileType {

    data class SimpleTemperature(val temperature : Boolean?) : TileType

    data class SimpleHumidity(val humidity : Boolean?) : TileType

    data class SimpleBinaryOnOff(val state : BinaryOnOffEnum?) : TileType

}