package com.tirexmurina.tilerboard.di

import com.tirexmurina.tilerboard.shared.sensor.data.SensorRepositoryImpl
import com.tirexmurina.tilerboard.shared.sensor.data.remote.models.SensorRemoteModelHelper
import com.tirexmurina.tilerboard.shared.sensor.domain.repository.SensorRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SensorModule {

    @Provides
    fun provideSensorRemoteConverter() : SensorRemoteModelHelper = SensorRemoteModelHelper()

    @Module
    @InstallIn(SingletonComponent::class)
    interface SensorModuleInt{
        @Binds
        @Singleton
        fun provideSensorRepository(repository: SensorRepositoryImpl) : SensorRepository
    }

}