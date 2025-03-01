package com.tirexmurina.tilerboard.shared.sensor.domain.repository

import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor

interface SensorRepository {

    suspend fun getSensorDataByNameId(nameId : String) : Sensor

    suspend fun getAllSensors() : List<Sensor>

}