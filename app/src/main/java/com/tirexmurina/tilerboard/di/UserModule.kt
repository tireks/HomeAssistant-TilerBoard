package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.database.core.storage.AppDatabase
import com.tirexmurina.tilerboard.shared.user.data.TokenRepositoryImpl
import com.tirexmurina.tilerboard.shared.user.data.UrlRepositoryImpl
import com.tirexmurina.tilerboard.shared.user.data.UserRepositoryImpl
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserDao
import com.tirexmurina.tilerboard.shared.user.domain.repository.TokenRepository
import com.tirexmurina.tilerboard.shared.user.domain.repository.UrlRepository
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) : UserDao = database.userDao()

    @Module
    @InstallIn(SingletonComponent::class)
    interface UserModuleInt {
        @Binds
        @Singleton
        fun provideUsersRepository(repository: UserRepositoryImpl) : UserRepository

        @Binds
        @Singleton
        fun provideTokenRepository(repository: TokenRepositoryImpl) : TokenRepository

        @Binds
        @Singleton
        fun provideUrlRepository(repository: UrlRepositoryImpl) : UrlRepository
    }

}