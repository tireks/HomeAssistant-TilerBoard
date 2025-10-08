package com.tirexmurina.tilerboard.features.home.presentation.homeScreen

import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

sealed interface HomeFlow {

    data class DynamicTiles(val listTile: List<Tile>) : HomeFlow

}