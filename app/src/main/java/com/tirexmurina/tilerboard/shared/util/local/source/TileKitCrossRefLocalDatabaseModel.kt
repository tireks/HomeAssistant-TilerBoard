package com.tirexmurina.tilerboard.shared.util.local.source

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "tile_kit_cross_ref",
    primaryKeys = ["tileId", "kitId"],
    indices = [
        Index(value = ["tileId"]),
        Index(value = ["kitId"])
    ]
)
data class TileKitCrossRefLocalDatabaseModel(
    val tileId: Long,
    val kitId: Long
)
