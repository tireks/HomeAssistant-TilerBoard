package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.shared.sensor.data.remote.source.SensorAPI
import com.tirexmurina.tilerboard.shared.util.remote.source.BASE_URL
import com.tirexmurina.tilerboard.shared.util.remote.source.CONNECT_TIMEOUT
import com.tirexmurina.tilerboard.shared.util.remote.source.READ_TIMEOUT
import com.tirexmurina.tilerboard.shared.util.remote.source.TokenizedAuthInterceptor
import com.tirexmurina.tilerboard.shared.util.remote.source.WRITE_TIMEOUT
import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenDataStore: TokenDataStore) : TokenizedAuthInterceptor =
        TokenizedAuthInterceptor(tokenDataStore)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: TokenizedAuthInterceptor): OkHttpClient =
        OkHttpClient().newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .cache(null)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideSensorsService(retrofit: Retrofit) : SensorAPI {
        return retrofit.create(SensorAPI::class.java)
    }
}