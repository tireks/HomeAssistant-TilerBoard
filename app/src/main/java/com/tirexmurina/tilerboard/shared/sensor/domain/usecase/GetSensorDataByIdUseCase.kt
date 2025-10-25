package com.tirexmurina.tilerboard.shared.sensor.domain.usecase

import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.repository.SensorRepository
import javax.inject.Inject

class GetSensorDataByIdUseCase @Inject constructor(
    private val repository: SensorRepository
) {
    suspend operator fun invoke(entityId : String) : Sensor = repository.getSensorDataByNameId(entityId)
}