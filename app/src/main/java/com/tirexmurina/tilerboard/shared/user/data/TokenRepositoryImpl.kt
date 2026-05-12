package com.tirexmurina.tilerboard.shared.user.data

import com.tirexmurina.tilerboard.shared.user.domain.repository.TokenRepository
import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : TokenRepository {
    override fun getAccessToken(): String? = tokenDataStore.getAccessToken()

    override fun setAccessToken(token: String) = tokenDataStore.setAccessToken(token)

    override fun isAccessTokenSaved(): Boolean = tokenDataStore.isAccessTokenSaved()
}
