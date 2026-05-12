package com.tirexmurina.tilerboard.shared.user.domain.repository

interface TokenRepository {
    fun getAccessToken(): String?

    fun setAccessToken(token: String)

    fun isAccessTokenSaved(): Boolean
}
