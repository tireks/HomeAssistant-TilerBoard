package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.domain.repository.UrlRepository
import com.tirexmurina.tilerboard.shared.util.remote.source.SavedUrlUnavailable
import javax.inject.Inject

class GetSavedUrlUseCase @Inject constructor(
    private val urlRepository: UrlRepository
) {
    operator fun invoke(): String? {
        try {
            return urlRepository.getBaseUrl()
        } catch (e : Exception) {
            throw SavedUrlUnavailable(e.message.toString())
        }
    }
}