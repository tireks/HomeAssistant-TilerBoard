package com.tirexmurina.tilerboard.shared.tile.data.local.models.converter

import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleHumidity
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleTemperature
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_BINARY_ON_OFF
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_HUMIDITY
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_TEMPERATURE

class TileLocalDatabaseModelHelper {

    fun buildTile(type: TileType): Tile {
        return Tile(
            id = 0,
            type = type
        )
    }
    fun toLocalModel(from : Tile, kitId : Long) : TileLocalDatabaseModel {
        val parsedPair = parseTileType(from.type)
        return TileLocalDatabaseModel(
            linkedKitId = kitId,
            type = parsedPair.first,
            universalContentField = parsedPair.second
        )
    }

    fun fromLocalModel(from : TileLocalDatabaseModel) : Tile {
        with(from){
            return Tile(
                id = id,
                type = rebuildEnumToTileType(type, universalContentField)
            )
        }
    }

    private fun parseTileType(type: TileType): Pair<TileTypeEnum, String> {
        return when (type) {
            is SimpleBinaryOnOff -> SIMPLE_BINARY_ON_OFF to type.state.toString()
            is SimpleHumidity -> SIMPLE_HUMIDITY to type.humidity.toString()
            is SimpleTemperature -> SIMPLE_TEMPERATURE to type.temperature.toString()
        }
    }

    private fun rebuildEnumToTileType(
        tileTypeEnum: TileTypeEnum,
        universalValue: String
    ): TileType {
        return when (tileTypeEnum) {
            SIMPLE_TEMPERATURE -> SimpleTemperature(universalValue.toDoubleOrNull())
            SIMPLE_HUMIDITY -> SimpleHumidity(universalValue.toDoubleOrNull())
            SIMPLE_BINARY_ON_OFF -> {
                when (universalValue) {
                    "ON" -> SimpleBinaryOnOff(BinaryOnOffEnum.ON)
                    "OFF" -> SimpleBinaryOnOff(BinaryOnOffEnum.OFF)
                    else -> SimpleBinaryOnOff(null)
                }
            }
        }
    }

}
