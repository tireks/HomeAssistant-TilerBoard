package com.tirexmurina.tilerboard.shared.user.data

import com.tirexmurina.tilerboard.shared.user.domain.repository.UrlRepository
import com.tirexmurina.tilerboard.source.remote.UrlDataStore
import javax.inject.Inject

class UrlRepositoryImpl @Inject constructor(
    private val urlDataStore: UrlDataStore
) : UrlRepository {
    override fun getBaseUrl(): String? = urlDataStore.getBaseUrl()

    override fun setBaseUrl(url: String) = urlDataStore.setBaseUrl(url)
}
