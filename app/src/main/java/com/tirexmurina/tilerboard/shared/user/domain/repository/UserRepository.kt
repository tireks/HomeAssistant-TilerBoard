package com.tirexmurina.tilerboard.shared.user.domain.repository

interface UserRepository {

    suspend fun getId(login : String) : Long?

    suspend fun create(login: String) : Long?

    suspend fun isApiAvailable() : Boolean

}