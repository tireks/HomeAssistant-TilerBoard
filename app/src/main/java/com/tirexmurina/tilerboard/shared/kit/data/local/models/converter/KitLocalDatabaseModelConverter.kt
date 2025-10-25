package com.tirexmurina.tilerboard.shared.kit.data.local.models.converter

import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit

class KitLocalDatabaseModelConverter {
    fun entityToLocalModel(from: Kit, userId : Long) : KitLocalDatabaseModel {
        with(from){
            return KitLocalDatabaseModel(
                linkedUserId = userId,
                name = name,
                iconResId = iconResId
            )
        }
    }

    fun localModelToEntity(from : KitLocalDatabaseModel) : Kit {
        with(from){
            return Kit(
                id = id,
                name = name,
                iconResId = iconResId
            )
        }
    }
}