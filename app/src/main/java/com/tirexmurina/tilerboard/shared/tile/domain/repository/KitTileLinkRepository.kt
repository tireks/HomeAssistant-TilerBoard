package com.tirexmurina.tilerboard.shared.tile.domain.repository

interface KitTileLinkRepository {
    suspend fun linkTileToKit(tileId: Long, kitId: Long)

    suspend fun detachTileFromKit(tileId: Long, kitId: Long)

    suspend fun clearTileLinks(tileId: Long)
}
