package com.tirexmurina.tilerboard.shared.user.domain.entity

import com.tirexmurina.tilerboard.shared.user.util.UserAccessLevel

data class User(
    val login : String,
    val access : UserAccessLevel
)
