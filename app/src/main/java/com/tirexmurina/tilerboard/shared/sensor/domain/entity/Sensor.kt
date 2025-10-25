package com.tirexmurina.tilerboard.shared.sensor.domain.entity

data class Sensor(
    val entityId: String,
    val lastChanged: String,
    val lastUpdated: String,
    val state: String,
    val deviceClass: String,
    val friendlyName: String,
    val unitOfMeasurement: String
)
