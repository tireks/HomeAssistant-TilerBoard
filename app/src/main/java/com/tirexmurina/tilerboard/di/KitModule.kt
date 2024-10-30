package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.database.core.storage.AppDatabase
import com.tirexmurina.tilerboard.shared.kit.data.KitRepositoryImpl
import com.tirexmurina.tilerboard.shared.kit.data.local.models.converter.KitLocalDatabaseModelConverter
import com.tirexmurina.tilerboard.shared.kit.data.local.source.KitDao
import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTilesByKitIdUseCase
import com.tirexmurina.tilerboard.shared.user.data.UserRepositoryImpl
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserDao
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class KitModule {

    @Provides
    @Singleton
    fun provideKitDao(database: AppDatabase) : KitDao = database.kitDao()

    @Provides
    fun provideKitLocalConverter() : KitLocalDatabaseModelConverter = KitLocalDatabaseModelConverter()

    @Module
    @InstallIn(SingletonComponent::class)
    interface KitModuleInt {
        @Binds
        @Singleton
        fun provideKitRepository(repository: KitRepositoryImpl) : KitRepository
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class KitDomainModule {
    @Provides
    fun provideGetTilesByKitIdUseCase(tileRepository: TileRepository) : GetTilesByKitIdUseCase = GetTilesByKitIdUseCase(tileRepository)
}