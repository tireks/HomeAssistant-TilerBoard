package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import javax.inject.Inject

class DeleteTileUseCase @Inject constructor(
    private val repository: TileRepository
) {
    suspend operator fun invoke(tileId: Long) = repository.deleteTile(tileId)
}
