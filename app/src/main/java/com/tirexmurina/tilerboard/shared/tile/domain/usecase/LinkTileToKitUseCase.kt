package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.repository.KitTileLinkRepository
import javax.inject.Inject

class LinkTileToKitUseCase @Inject constructor(
    private val repository: KitTileLinkRepository
) {
    suspend operator fun invoke(tileId: Long, kitId: Long) = repository.linkTileToKit(tileId, kitId)
}
