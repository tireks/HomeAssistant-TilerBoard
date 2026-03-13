package com.tirexmurina.tilerboard.shared.kit.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel


@Dao
interface KitDao {

    @Query("SELECT * FROM kits WHERE linkedUserId = :userId")
    suspend fun getKitsByUserId(userId: Long): List<KitLocalDatabaseModel>

    @Query("SELECT COUNT(*) FROM kits WHERE linkedUserId = :userId")
    suspend fun getKitCountByUserId(userId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createKit(kit : KitLocalDatabaseModel): Long

    @Update
    suspend fun updateKit(kit: KitLocalDatabaseModel)

    @Query("DELETE FROM kits WHERE id = :kitId")
    suspend fun deleteKit(kitId: Long)

    @Query("SELECT * FROM kits WHERE id = :kitId LIMIT 1")
    suspend fun getKitById(kitId: Long): KitLocalDatabaseModel?

    @Query("DELETE FROM tile_kit_cross_ref WHERE kitId = :kitId")
    suspend fun clearKitTileLinks(kitId: Long)

}
