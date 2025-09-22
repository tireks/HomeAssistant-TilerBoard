package com.tirexmurina.tilerboard.shared.util.remote.source

import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import okhttp3.Interceptor
import okhttp3.Response

class TokenizedAuthInterceptor(private val tokenDataStore: TokenDataStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenDataStore.getAccessToken()
        if (token == null) throw TokenCorruptedOrUnavailable("Failed to read token from dataStore")
        val originalRequest = chain.request()
        val requestWithToken = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(requestWithToken)
    }
}