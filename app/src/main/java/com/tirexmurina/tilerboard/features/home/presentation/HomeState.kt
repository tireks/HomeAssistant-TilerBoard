package com.tirexmurina.tilerboard.features.home.presentation

import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit

sealed interface HomeState {

    data object Initial : HomeState

    sealed interface Error : HomeState {

        data object UnknownError : Error

        data object TestError : Error

    }

    data class StaticContent(val listKit : List<Kit>) : HomeState

    data class DynamicContent()

}