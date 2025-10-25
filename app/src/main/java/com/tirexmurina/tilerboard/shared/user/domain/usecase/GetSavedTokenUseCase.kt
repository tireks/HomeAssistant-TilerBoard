package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.util.remote.source.TokenCorruptedOrUnavailable
import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import javax.inject.Inject

class GetSavedTokenUseCase @Inject constructor(
    private val tokenDataStore: TokenDataStore
) {
    operator fun invoke(): String? {
        try {
            return tokenDataStore.getAccessToken()
        } catch (e: Exception) {
            throw TokenCorruptedOrUnavailable(e.message.toString())
        }
    }
}