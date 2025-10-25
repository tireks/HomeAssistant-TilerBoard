package com.tirexmurina.tilerboard.shared.kit.domain.entity

import androidx.annotation.DrawableRes

data class Kit(
    val id : Long,
    val name: String,
    @DrawableRes val iconResId: Int
)
