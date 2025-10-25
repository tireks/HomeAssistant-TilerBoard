package com.tirexmurina.tilerboard.shared.tile.util

enum class BinaryOnOffEnum{
    ON,
    OFF
}

fun chooseBinaryOnOffEnum(state: String) : BinaryOnOffEnum {
    return if (state == "on") {
        BinaryOnOffEnum.ON
    } else BinaryOnOffEnum.OFF
}