package com.tirexmurina.tilerboard.shared.tile.util

sealed interface TileType {

    data class TemperatureSimple(val temperature : Boolean) : TileType

    data class HumiditySimple(val humidity : Boolean) : TileType

    data class BinarySimpleOnOff(val state : BinaryEnumOnOff) : TileType

}