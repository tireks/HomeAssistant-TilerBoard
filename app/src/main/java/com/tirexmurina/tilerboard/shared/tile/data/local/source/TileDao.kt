package com.tirexmurina.tilerboard.shared.tile.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tirexmurina.tilerboard.shared.tile.data.local.models.KitWithTilesLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileKitCrossRefLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel

@Dao
interface TileDao {

    @Transaction
    @Query("SELECT * FROM kits WHERE id = :kitId")
    suspend fun getKitWithTilesByKitId(kitId: Long): KitWithTilesLocalDatabaseModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTile(tile : TileLocalDatabaseModel): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkTileToKit(crossRef: TileKitCrossRefLocalDatabaseModel)

    @Query("SELECT COUNT(*) FROM tile_kit_cross_ref WHERE kitId = :kitId")
    suspend fun getTileLinksCountByKitId(kitId: Long): Int

    @Query("DELETE FROM tile_kit_cross_ref WHERE tileId = :tileId AND kitId = :kitId")
    suspend fun unlinkTileFromKit(tileId: Long, kitId: Long): Int

    @Query(
        """
        DELETE FROM tiles
        WHERE id = :tileId
        AND NOT EXISTS (
            SELECT 1 FROM tile_kit_cross_ref WHERE tileId = :tileId
        )
        """
    )
    suspend fun deleteTileIfOrphan(tileId: Long): Int
}
