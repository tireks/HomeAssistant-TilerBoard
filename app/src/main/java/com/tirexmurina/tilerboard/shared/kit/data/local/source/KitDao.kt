package com.tirexmurina.tilerboard.shared.kit.data.local.source

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel


interface KitDao {

    @Query("SELECT * FROM kits WHERE linkedUserId = :userId")
    suspend fun getKitsByUserId(userId: Long): List<KitLocalDatabaseModel>

    @Query("SELECT COUNT(*) FROM kits WHERE linkedUserId = :userId")
    suspend fun getKitCountByUserId(userId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createKit(kit : KitLocalDatabaseModel): Long

}