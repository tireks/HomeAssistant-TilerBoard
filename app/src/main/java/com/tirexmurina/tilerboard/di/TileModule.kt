package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.database.core.storage.AppDatabase
import com.tirexmurina.tilerboard.shared.tile.data.TileRepositoryImpl
import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelHelper
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TileModule {

    @Provides
    @Singleton
    fun provideTileDao(database: AppDatabase) : TileDao = database.tileDao()

    @Provides
    fun provideTileLocalConverter() : TileLocalDatabaseModelHelper = TileLocalDatabaseModelHelper()

    @Module
    @InstallIn(SingletonComponent::class)
    interface TileModuleInt {
        @Binds
        @Singleton
        fun provideTileRepository(repository: TileRepositoryImpl) : TileRepository
    }

}
