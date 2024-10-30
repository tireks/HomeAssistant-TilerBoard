package com.tirexmurina.tilerboard.shared.tile.data.local.source

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel

interface TileDao {

    @Query("SELECT * FROM tiles WHERE linkedKitId = :kitId")
    suspend fun getTilesByUserId(kitId: Long): List<TileLocalDatabaseModel>

    @Query("SELECT COUNT(*) FROM tiles WHERE linkedKitId = :kitId")
    suspend fun getTilesCountByUserId(kitId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTile(kit : TileLocalDatabaseModel): Long

}