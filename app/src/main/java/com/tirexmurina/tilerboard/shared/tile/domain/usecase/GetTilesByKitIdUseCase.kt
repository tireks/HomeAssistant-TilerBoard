package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.sensor.domain.repository.SensorRepository
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import javax.inject.Inject

class GetTilesByKitIdUseCase @Inject constructor (
    private val tileRepository: TileRepository,
    private  val sensorRepository: SensorRepository
){
    suspend operator fun invoke(kitId : Long) : List<Tile> /*= repository.getTilesByKitId(kitId)*/ {
        val blankTileList = tileRepository.getTilesByKitId(kitId)
        val readyTileList = blankTileList.map {
            val sensor = sensorRepository.getSensorDataByNameId(it.linkedSensorEntityId)
            val tile = Tile(
                id = it.id,
                type = it.type,
                sensor = sensor
            )
            tile
        }
        return readyTileList
    }
}