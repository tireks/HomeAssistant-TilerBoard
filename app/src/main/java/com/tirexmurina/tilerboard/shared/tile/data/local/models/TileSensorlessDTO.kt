package com.tirexmurina.tilerboard.shared.tile.data.local.models

import com.tirexmurina.tilerboard.shared.tile.util.TileType

data class TileSensorlessDTO (
    val id : Long,
    val type: TileType,
    val linkedSensorEntityId: String
)