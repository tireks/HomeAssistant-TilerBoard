package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import javax.inject.Inject

class GetTileByIdUseCase @Inject constructor(
    private val repository: TileRepository
) {
    suspend operator fun invoke(tileId: Long): Tile = repository.getTileById(tileId)
}
