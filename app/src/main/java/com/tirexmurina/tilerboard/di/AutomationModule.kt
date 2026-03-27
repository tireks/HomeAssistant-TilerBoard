package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.database.core.storage.AppDatabase
import com.tirexmurina.tilerboard.shared.automation.data.AutomationRepositoryImpl
import com.tirexmurina.tilerboard.shared.automation.data.local.source.AutomationDao
import com.tirexmurina.tilerboard.shared.automation.domain.repository.AutomationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AutomationModule {

    @Provides
    @Singleton
    fun provideAutomationDao(appDatabase: AppDatabase): AutomationDao = appDatabase.automationDao()

    @Module
    @InstallIn(SingletonComponent::class)
    interface AutomationModuleInt {
        @Binds
        @Singleton
        fun provideAutomationRepository(repository: AutomationRepositoryImpl): AutomationRepository
    }
}
