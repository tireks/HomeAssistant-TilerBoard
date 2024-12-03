package com.tirexmurina.tilerboard.shared.tile.data.local.models.converter

import com.tirexmurina.tilerboard.shared.tile.data.local.models.SimpleSwitchOnOffDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleHumidity
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleTemperature
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_BINARY_ON_OFF
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_HUMIDITY
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_TEMPERATURE
import com.tirexmurina.tilerboard.shared.tile.util.UnexpectedTileType

class TileLocalDatabaseModelHelper {

    fun buildTile(type: TileType): Tile {
        return Tile(
            id = 0,
            type = type
        )
    }
    fun toLocalModel(from : Tile, kitId : Long) : TileLocalDatabaseModel {
        return TileLocalDatabaseModel(
            linkedKitId = kitId,
            type = convertTileTypeToEnum(from.type)
        )
    }

    fun fromLocalModel(from : TileLocalDatabaseModel, typeRawContainment: Any) : Tile {
        with(from){
            return Tile(
                id = id,
                type = rebuildEnumToTileType(type, typeRawContainment)
            )
        }
    }

    fun convertTileTypeToEnum(type: TileType) : TileTypeEnum{
        return when (type){
            is SimpleBinaryOnOff -> SIMPLE_BINARY_ON_OFF
            is SimpleHumidity -> SIMPLE_HUMIDITY
            is SimpleTemperature -> SIMPLE_TEMPERATURE
        }
    }

    fun rebuildEnumToTileType(enumType : TileTypeEnum, typeRawContainment: Any) : TileType {
        when (enumType){
            SIMPLE_TEMPERATURE -> TODO()
            SIMPLE_HUMIDITY -> TODO()
            SIMPLE_BINARY_ON_OFF -> {
                if (typeRawContainment is SimpleSwitchOnOffDatabaseModel){
                    return SimpleBinaryOnOff(typeRawContainment.state)
                }
            }
        }
        throw UnexpectedTileType("unexpected tile type acquired on converting tile from DB")
    }
}