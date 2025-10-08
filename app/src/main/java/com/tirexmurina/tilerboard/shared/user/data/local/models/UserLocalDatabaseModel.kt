package com.tirexmurina.tilerboard.shared.user.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserLocalDatabaseModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val login : String
)
