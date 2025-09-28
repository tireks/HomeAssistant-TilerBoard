package com.tirexmurina.tilerboard.shared.tile.domain.entity

import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.tile.util.TileType

data class Tile(
    val id : Long,
    val type: TileType,
    val sensor: Sensor //todo теперь у нас в тайле есть два источника данных для стейта, нужно подумать над этим
)
