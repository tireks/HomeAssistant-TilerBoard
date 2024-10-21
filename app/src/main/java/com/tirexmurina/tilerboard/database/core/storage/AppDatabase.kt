package com.tirexmurina.tilerboard.database.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tirexmurina.tilerboard.shared.user.data.local.models.UserLocalDatabaseModel

@Database(
    entities = [UserLocalDatabaseModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

}