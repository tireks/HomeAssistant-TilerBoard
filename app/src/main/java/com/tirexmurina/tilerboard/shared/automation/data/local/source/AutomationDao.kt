package com.tirexmurina.tilerboard.shared.automation.data.local.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tirexmurina.tilerboard.shared.automation.data.local.models.AutomationLocalDatabaseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationDao {

    @Query("SELECT * FROM automations WHERE linked_user_id = :userId ORDER BY id DESC")
    fun observeAll(userId: Long): Flow<List<AutomationLocalDatabaseModel>>

    @Query("SELECT * FROM automations WHERE linked_user_id = :userId ORDER BY id DESC")
    suspend fun getAll(userId: Long): List<AutomationLocalDatabaseModel>

    @Query("SELECT * FROM automations WHERE linked_user_id = :userId AND enabled = 1")
    suspend fun getEnabled(userId: Long): List<AutomationLocalDatabaseModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(automation: AutomationLocalDatabaseModel): Long

    @Update
    suspend fun update(automation: AutomationLocalDatabaseModel)

    @Delete
    suspend fun delete(automation: AutomationLocalDatabaseModel)

    @Query("DELETE FROM automations WHERE id = :automationId AND linked_user_id = :userId")
    suspend fun deleteById(automationId: Long, userId: Long)

    @Query("UPDATE automations SET enabled = :enabled WHERE id = :automationId AND linked_user_id = :userId")
    suspend fun setEnabled(automationId: Long, enabled: Boolean, userId: Long)
}
