package com.tirexmurina.tilerboard.shared.sensor.domain.entity

data class Sensor(
    val nameId : String,
    /**
     * пока кажется, что здесь использование жестких типов избыточно
     * мы всё равно будем юзать эти данные при сборке flow, там и будем это оборачивать в правильный тип tile
     */
    //val type : SensorType
    val state : String
)
