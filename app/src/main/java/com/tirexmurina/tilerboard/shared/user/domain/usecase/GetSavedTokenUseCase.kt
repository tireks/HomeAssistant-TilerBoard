package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.domain.repository.TokenRepository
import com.tirexmurina.tilerboard.shared.util.remote.source.TokenCorruptedOrUnavailable
import javax.inject.Inject

class GetSavedTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): String? {
        try {
            return tokenRepository.getAccessToken()
        } catch (e: Exception) {
            throw TokenCorruptedOrUnavailable(e.message.toString())
        }
    }
}