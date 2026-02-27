package com.tirexmurina.tilerboard.shared.tile.domain.repository

import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileSensorlessDTO
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType

interface TileRepository {

    suspend fun getTilesByKitId(kitId: Long): List<TileSensorlessDTO>

    suspend fun getAllTiles(): List<Tile>

    suspend fun getTileById(tileId: Long): Tile

    suspend fun createTile(type: TileType, linkedSensorId: String, name: String?): Long

    suspend fun linkTileToKit(tileId: Long, kitId: Long)

    suspend fun detachTileFromKit(tileId: Long, kitId: Long)
}
