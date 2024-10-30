package com.tirexmurina.tilerboard.shared.tile.domain.repository

import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType

interface TileRepository {

    suspend fun getTilesByKitId(kitId : Long) : List<Tile>

    suspend fun createTile(type: TileType, kitId: Long)

}