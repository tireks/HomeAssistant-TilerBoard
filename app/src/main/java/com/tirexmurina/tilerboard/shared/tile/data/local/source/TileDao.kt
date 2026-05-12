package com.tirexmurina.tilerboard.shared.tile.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.util.local.source.KitWithTilesLocalDatabaseModel

@Dao
interface TileDao {

    @Transaction
    @Query("SELECT * FROM kits WHERE id = :kitId")
    suspend fun getKitWithTilesByKitId(kitId: Long): KitWithTilesLocalDatabaseModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTile(tile: TileLocalDatabaseModel): Long

    @Update
    suspend fun updateTile(tile: TileLocalDatabaseModel)

    @Query("DELETE FROM tiles WHERE id = :tileId")
    suspend fun deleteTile(tileId: Long)

    @Query("SELECT * FROM tiles")
    suspend fun getAllTiles(): List<TileLocalDatabaseModel>

    @Query("SELECT * FROM tiles WHERE id = :tileId LIMIT 1")
    suspend fun getTileById(tileId: Long): TileLocalDatabaseModel?
}
