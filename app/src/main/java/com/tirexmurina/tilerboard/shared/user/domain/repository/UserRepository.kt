package com.tirexmurina.tilerboard.shared.user.domain.repository

import com.tirexmurina.tilerboard.shared.user.util.UserAccessLevel

interface UserRepository {

    suspend fun authUserLocal(login : String, accessLevel: UserAccessLevel)

}