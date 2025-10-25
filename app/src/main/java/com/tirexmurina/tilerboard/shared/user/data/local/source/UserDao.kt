package com.tirexmurina.tilerboard.shared.user.data.local.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tirexmurina.tilerboard.shared.user.data.local.models.UserLocalDatabaseModel

@Dao
interface UserDao {

    @Query("SELECT id FROM users WHERE login = :login LIMIT 1")
    suspend fun getUserId(login : String) : Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(user : UserLocalDatabaseModel): Long

    @Query("SELECT EXISTS(SELECT 1 FROM users LIMIT 1)")
    suspend fun isAnyUserPresent(): Boolean
}