package com.tirexmurina.tilerboard.shared.tile.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirexmurina.tilerboard.shared.tile.data.local.models.SimpleSwitchOnOffDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel

@Dao
interface TileDao {

    @Query("SELECT * FROM tiles WHERE linkedKitId = :kitId")
    suspend fun getTilesByUserId(kitId: Long): List<TileLocalDatabaseModel>

    @Query("SELECT COUNT(*) FROM tiles WHERE linkedKitId = :kitId")
    suspend fun getTilesCountByUserId(kitId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTile(tile : TileLocalDatabaseModel): Long

    @Query("SELECT * FROM tiles_types_simple_switches_on_off WHERE linkedTileId = :tileId")
    suspend fun getSimpleSwitchOnOffByTileId(tileId: Long): SimpleSwitchOnOffDatabaseModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createSimpleSwitchOnOff(simpleSwitchOnOffDatabaseModel: SimpleSwitchOnOffDatabaseModel)



}