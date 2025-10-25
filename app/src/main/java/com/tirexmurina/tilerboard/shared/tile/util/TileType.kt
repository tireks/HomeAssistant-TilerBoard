package com.tirexmurina.tilerboard.shared.tile.util

/**
 * добавил сюда - добавь и в TileTypeEnum
 */
sealed interface TileType {

    data class SimpleTemperature(val temperature : Double?) : TileType

    data class SimpleHumidity(val humidity : Double?) : TileType

    data class SimpleBinaryOnOff(val state : BinaryOnOffEnum?) : TileType

    data class SimpleNoTypeRaw(val state : String?) : TileType

}