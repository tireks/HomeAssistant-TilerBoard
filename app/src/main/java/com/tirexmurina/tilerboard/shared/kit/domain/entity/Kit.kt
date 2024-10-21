package com.tirexmurina.tilerboard.shared.kit.domain.entity

import androidx.annotation.DrawableRes

data class Kit(
    val name: String,
    @DrawableRes val iconResId: Int
)
