package com.tirexmurina.tilerboard.shared.sensor.data.remote.models

data class SensorRemoteModelTemp(
    val attributes: Attributes,
    val context: SensorContext,
    val entity_id: String,
    val last_changed: String?,
    val last_updated: String?,
    val state: String?
)

data class SensorContext(
    val id: String,
    val parent_id: String?,
    val user_id: String?
)

data class Attributes(
    val device_class: String?,
    val friendly_name: String?,
    val unit_of_measurement: String?
)