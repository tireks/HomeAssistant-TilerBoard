package com.tirexmurina.tilerboard.di

import android.content.Context
import com.tirexmurina.tilerboard.database.core.storage.AppDatabase
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import com.tirexmurina.tilerboard.source.remote.UrlDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStoreModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext app: Context): AppDatabase {
        return AppDatabase.getDatabase(app)
    }

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext app: Context) : TokenDataStore = TokenDataStore(app)

    @Provides
    @Singleton
    fun provideUrlDataStore(@ApplicationContext app: Context) : UrlDataStore = UrlDataStore(app)

    @Provides
    @Singleton
    fun provideUserIdDataStore() : UserIdDataStore = UserIdDataStore()
}