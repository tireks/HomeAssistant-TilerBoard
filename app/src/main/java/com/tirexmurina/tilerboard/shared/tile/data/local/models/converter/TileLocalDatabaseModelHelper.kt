package com.tirexmurina.tilerboard.shared.tile.data.local.models.converter

import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileSensorlessDTO
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleHumidity
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleNoTypeRaw
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleTemperature
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_BINARY_ON_OFF
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_HUMIDITY
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_NO_TYPE_RAW
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_TEMPERATURE

class TileLocalDatabaseModelHelper {
    fun buildTileDbModel(type: TileType, kitId: Long, linkedSensorId: String, name: String?): TileLocalDatabaseModel {
        return TileLocalDatabaseModel(
            linkedKitId = kitId,
            type = parseTileType(type),
            linkedSensorEntityId = linkedSensorId,
            name = name
        )
    }

    fun fromLocalModel(from : TileLocalDatabaseModel) : TileSensorlessDTO {
        with(from){
            return TileSensorlessDTO(
                id = id,
                type = rebuildEnumToTileType(type),
                linkedSensorEntityId = linkedSensorEntityId,
                name = name
            )
        }
    }

    private fun parseTileType(type: TileType): TileTypeEnum {
        return when (type) {
            is SimpleBinaryOnOff -> SIMPLE_BINARY_ON_OFF
            is SimpleHumidity -> SIMPLE_HUMIDITY
            is SimpleTemperature -> SIMPLE_TEMPERATURE
            is SimpleNoTypeRaw -> SIMPLE_NO_TYPE_RAW
        }
    }

    private fun rebuildEnumToTileType(
        tileTypeEnum: TileTypeEnum
    ): TileType {
        return when (tileTypeEnum) {
            SIMPLE_TEMPERATURE -> SimpleTemperature(null)
            SIMPLE_HUMIDITY -> SimpleHumidity(null)
            SIMPLE_BINARY_ON_OFF -> SimpleBinaryOnOff(null)
            SIMPLE_NO_TYPE_RAW -> SimpleNoTypeRaw(null)
        }
    }

}
