package com.tirexmurina.tilerboard.shared.tile.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirexmurina.tilerboard.shared.util.local.source.TileKitCrossRefLocalDatabaseModel

@Dao
interface KitTileLinkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkTileToKit(crossRef: TileKitCrossRefLocalDatabaseModel)

    @Query("DELETE FROM tile_kit_cross_ref WHERE tileId = :tileId AND kitId = :kitId")
    suspend fun unlinkTileFromKit(tileId: Long, kitId: Long)

    @Query("DELETE FROM tile_kit_cross_ref WHERE tileId = :tileId")
    suspend fun clearTileLinks(tileId: Long)

    @Query("SELECT COUNT(*) FROM tile_kit_cross_ref WHERE kitId = :kitId")
    suspend fun getTileLinksCountByKitId(kitId: Long): Int
}
