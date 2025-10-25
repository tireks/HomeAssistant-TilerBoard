package com.tirexmurina.tilerboard.shared.tile.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel

@Dao
interface TileDao {

    @Query("SELECT * FROM tiles WHERE linkedKitId = :kitId")
    suspend fun getTilesByKitId(kitId: Long): List<TileLocalDatabaseModel>

    @Query("SELECT COUNT(*) FROM tiles WHERE linkedKitId = :kitId")
    suspend fun getTilesCountByKitId(kitId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTile(tile : TileLocalDatabaseModel): Long
}