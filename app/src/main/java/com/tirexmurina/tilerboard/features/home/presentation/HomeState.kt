package com.tirexmurina.tilerboard.features.home.presentation

import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

sealed interface HomeState {

    data object Initial : HomeState

    sealed interface Error : HomeState {

        data object UnknownError : Error

        data object TestError : Error

    }

    data class Content(
        val staticKitList: StaticKitList,
        val dynamicTilesList : DynamicTileList
    ) : HomeState

}

sealed interface StaticKitList {

    data object Loading : StaticKitList

    data class Content(val listKits : List<Kit>) : StaticKitList

    sealed interface Error : StaticKitList {

        data object TestKitError : Error
    }

}

sealed interface DynamicTileList {

    data object Loading : DynamicTileList

    data class Content(val listTiles : List<Tile>) : DynamicTileList

    sealed interface Error : DynamicTileList {

        data object TestTileError : Error
    }

}