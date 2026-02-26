package com.tirexmurina.tilerboard.shared.util.local.source

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileLocalDatabaseModel

data class KitWithTilesLocalDatabaseModel(
    @Embedded
    val kit: KitLocalDatabaseModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TileKitCrossRefLocalDatabaseModel::class,
            parentColumn = "kitId",
            entityColumn = "tileId"
        )
    )
    val tiles: List<TileLocalDatabaseModel>
)
