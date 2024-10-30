package com.tirexmurina.tilerboard.features.home.presentation

import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

sealed interface HomeFlow {

    sealed interface Content : HomeFlow {

        data class StaticKits(val listKit : List<Kit>) : Content

        data class DynamicTiles(val listTile : List<Tile>) : Content

    }

}