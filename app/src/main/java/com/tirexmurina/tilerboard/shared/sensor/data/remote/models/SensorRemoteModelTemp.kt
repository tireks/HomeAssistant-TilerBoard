package com.tirexmurina.tilerboard.shared.sensor.data.remote.models

data class SensorRemoteModelTemp(
    val attributes: Attributes,
    val context: Context,
    val entity_id: String,
    val last_changed: String,
    val last_updated: String,
    val state: String
)

data class Context(
    val id: String,
    val parent_id: Any,
    val user_id: Any
)

data class Attributes(
    val device_class: String,
    val friendly_name: String,
    val unit_of_measurement: String
)