package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import javax.inject.Inject

class CreateTileUseCase @Inject constructor (
    private val repository: TileRepository
) {
    suspend operator fun invoke(type: TileType, kitId: Long, linkedSensorId: String, name: String?) =
        repository.createTile(type, kitId, linkedSensorId, name)
}