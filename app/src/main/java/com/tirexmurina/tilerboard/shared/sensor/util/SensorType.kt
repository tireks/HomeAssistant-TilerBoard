package com.tirexmurina.tilerboard.shared.sensor.util

import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum

sealed interface SensorType {

    data class SimpleTemperature(val temperature : Boolean?) : SensorType

    data class SimpleHumidity(val humidity : Boolean?) : SensorType

    data class SimpleBinaryOnOff(val state : BinaryOnOffEnum?) : SensorType

}