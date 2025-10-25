package com.tirexmurina.tilerboard.shared.util.remote.source

import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import com.tirexmurina.tilerboard.source.remote.UrlDataStore
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class TokenizedAuthInterceptor(
    private val tokenDataStore: TokenDataStore,
    private val urlDataStore: UrlDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenDataStore.getAccessToken()
            ?: throw TokenCorruptedOrUnavailable("Failed to read token from dataStore")

        var request = chain.request()

        urlDataStore.getBaseUrl()?.let { baseUrl ->
            val newBase = baseUrl.toHttpUrlOrNull()
                ?: throw IllegalArgumentException("Invalid base url: $baseUrl")

            val newUrl = request.url.newBuilder()
                .scheme(newBase.scheme)
                .host(newBase.host)
                .port(newBase.port)
                .build()

            request = request.newBuilder().url(newUrl).build()
        }

        request = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}