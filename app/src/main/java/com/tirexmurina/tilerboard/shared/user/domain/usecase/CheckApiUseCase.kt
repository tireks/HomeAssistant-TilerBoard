package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.domain.repository.TokenRepository
import com.tirexmurina.tilerboard.shared.user.domain.repository.UrlRepository
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import javax.inject.Inject

class CheckApiUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val urlRepository: UrlRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token : String, url: String) : Boolean {
        try {
            tokenRepository.setAccessToken(token)
            urlRepository.setBaseUrl(url)
            return userRepository.isApiAvailable()
        } catch ( e : Exception) {
            throw e
        }
    }
}