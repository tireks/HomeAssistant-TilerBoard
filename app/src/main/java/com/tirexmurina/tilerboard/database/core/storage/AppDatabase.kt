package com.tirexmurina.tilerboard.database.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.kit.data.local.source.KitDao
import com.tirexmurina.tilerboard.shared.tile.data.local.models.SimpleSwitchOnOffDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.user.data.local.models.UserLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserDao

@Database(
    entities = [
        UserLocalDatabaseModel::class,
        KitLocalDatabaseModel::class,
        TileLocalDatabaseModel::class,
        SimpleSwitchOnOffDatabaseModel::class
               ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun kitDao(): KitDao

    abstract fun tileDao(): TileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}