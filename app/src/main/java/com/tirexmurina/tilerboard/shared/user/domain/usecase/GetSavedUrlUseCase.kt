package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.util.remote.source.SavedUrlUnavailable
import com.tirexmurina.tilerboard.source.remote.UrlDataStore
import javax.inject.Inject

class GetSavedUrlUseCase @Inject constructor(
    private val urlDataStore: UrlDataStore
) {
    operator fun invoke(): String? {
        try {
            return urlDataStore.getBaseUrl()
        } catch (e : Exception) {
            throw SavedUrlUnavailable(e.message.toString())
        }
    }
}