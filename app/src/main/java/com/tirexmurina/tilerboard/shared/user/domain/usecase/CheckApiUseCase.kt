package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import com.tirexmurina.tilerboard.source.remote.UrlDataStore
import javax.inject.Inject

class CheckApiUseCase @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val urlDataStore: UrlDataStore,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token : String, url: String) : Boolean {
        try {
            tokenDataStore.setAccessToken(token)
            urlDataStore.setBaseUrl(url)
            return userRepository.isApiAvailable()
        } catch ( e : Exception) {
            throw e
        }
    }
}