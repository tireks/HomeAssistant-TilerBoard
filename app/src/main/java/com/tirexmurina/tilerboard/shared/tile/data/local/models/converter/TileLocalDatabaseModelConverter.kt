package com.tirexmurina.tilerboard.shared.tile.data.local.models.converter

import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

class TileLocalDatabaseModelConverter {
    fun entityToLocalModel(from : Tile, kitId : Long) : TileLocalDatabaseModel {
        with(from){
            return TileLocalDatabaseModel(
                linkedKitId = kitId,
                type = type
            )
        }
    }

    fun localModelToEntity(from : TileLocalDatabaseModel) : Tile {
        with(from){
            return Tile(
                id = id,
                type = type
            )
        }
    }
}