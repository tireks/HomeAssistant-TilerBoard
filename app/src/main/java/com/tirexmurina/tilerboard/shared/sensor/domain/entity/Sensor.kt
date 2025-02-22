package com.tirexmurina.tilerboard.shared.sensor.domain.entity

import com.tirexmurina.tilerboard.shared.sensor.util.SensorType

data class Sensor(
    val id : Long,
    val nameId : String,
    val type : SensorType
)
