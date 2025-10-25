package com.tirexmurina.tilerboard.shared.kit.domain.repository

import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit

interface KitRepository {

    suspend fun getKits() : List<Kit>

    suspend fun createKit(name: String, iconResId : Int)

}