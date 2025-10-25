package com.tirexmurina.tilerboard.shared.tile.data.local.models 

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum

@Entity(tableName = "tiles")
data class TileLocalDatabaseModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val linkedKitId: Long,
    val type: TileTypeEnum,
    val name: String?,
    val linkedSensorEntityId: String
)
