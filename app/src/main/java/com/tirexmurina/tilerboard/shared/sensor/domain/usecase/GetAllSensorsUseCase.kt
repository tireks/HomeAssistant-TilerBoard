package com.tirexmurina.tilerboard.shared.sensor.domain.usecase

import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.repository.SensorRepository
import javax.inject.Inject

class GetAllSensorsUseCase @Inject constructor(
    private val repository: SensorRepository
) {
    suspend operator fun invoke() : List<Sensor> = repository.getAllSensors()
}