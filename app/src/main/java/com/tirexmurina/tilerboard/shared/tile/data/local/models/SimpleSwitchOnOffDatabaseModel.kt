package com.tirexmurina.tilerboard.shared.tile.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum

@Entity(tableName = "tiles_types_simple_switches_on_off")
data class SimpleSwitchOnOffDatabaseModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val linkedTileId: Long,
    val state: BinaryOnOffEnum?
)

