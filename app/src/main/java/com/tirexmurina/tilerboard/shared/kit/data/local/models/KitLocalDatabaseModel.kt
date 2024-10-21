package com.tirexmurina.tilerboard.shared.kit.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "kits")
data class KitLocalDatabaseModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val linkedUserId : Long,
    val name: String,
    val iconResId: Int
)
