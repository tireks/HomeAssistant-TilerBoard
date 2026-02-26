package com.tirexmurina.tilerboard.shared.tile.domain.repository

import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileSensorlessDTO
import com.tirexmurina.tilerboard.shared.tile.util.TileType

interface TileRepository {

    suspend fun getTilesByKitId(kitId : Long) : List<TileSensorlessDTO>

    suspend fun createTile(type: TileType, kitId: Long, linkedSensorId : String, name: String?)

    suspend fun detachTileFromKit(tileId: Long, kitId: Long)

}