package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.database.core.storage.AppDatabase
import com.tirexmurina.tilerboard.shared.kit.data.KitRepositoryImpl
import com.tirexmurina.tilerboard.shared.kit.data.local.models.converter.KitLocalDatabaseModelConverter
import com.tirexmurina.tilerboard.shared.kit.data.local.source.KitDao
import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.tile.data.TileRepositoryImpl
import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelConverter
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TileModule {

    @Provides
    @Singleton
    fun provideTileDao(database: AppDatabase) : TileDao = database.tileDao()

    @Provides
    fun provideTileLocalConverter() : TileLocalDatabaseModelConverter = TileLocalDatabaseModelConverter()

    @Module
    @InstallIn(SingletonComponent::class)
    interface TileModuleInt {
        @Binds
        @Singleton
        fun provideTileRepository(repository: TileRepositoryImpl) : TileRepository
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class TileDomainModule {
    @Provides
    fun provideGetKitsUseCase(kitRepository: KitRepository) : GetKitsUseCase = GetKitsUseCase(kitRepository)
}