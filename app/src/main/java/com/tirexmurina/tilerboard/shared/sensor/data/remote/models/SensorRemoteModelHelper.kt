package com.tirexmurina.tilerboard.shared.sensor.data.remote.models

import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor

class SensorRemoteModelHelper {

    fun fromRemoteModel(from : SensorRemoteModelTemp) : Sensor {
        return Sensor(
            nameId = from.entity_id,

        )
    }

}