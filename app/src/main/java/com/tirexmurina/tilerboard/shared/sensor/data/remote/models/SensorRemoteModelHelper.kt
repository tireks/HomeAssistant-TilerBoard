package com.tirexmurina.tilerboard.shared.sensor.data.remote.models

import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor

class SensorRemoteModelHelper {

    fun fromRemoteModel(from : SensorRemoteModelTemp) : Sensor {
        return Sensor(
            entityId = from.entity_id,
            lastChanged = from.last_changed ?: "",
            lastUpdated = from.last_updated ?: "",
            state = from.state ?: "",
            deviceClass = from.attributes.device_class ?: "",
            friendlyName = from.attributes.friendly_name ?: "",
            unitOfMeasurement = from.attributes.unit_of_measurement ?: ""
        )
    }

}