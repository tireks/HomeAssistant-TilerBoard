package com.tirexmurina.tilerboard.shared.user.domain.repository

interface UrlRepository {
    fun getBaseUrl(): String?

    fun setBaseUrl(url: String)
}
