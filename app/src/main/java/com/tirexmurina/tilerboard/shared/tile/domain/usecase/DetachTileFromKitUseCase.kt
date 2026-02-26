package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import javax.inject.Inject

class DetachTileFromKitUseCase @Inject constructor(
    private val tileRepository: TileRepository
) {
    suspend operator fun invoke(tileId: Long, kitId: Long) {
        tileRepository.detachTileFromKit(tileId, kitId)
    }
}
