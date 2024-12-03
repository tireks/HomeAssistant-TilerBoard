package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import javax.inject.Inject

class GetTilesByKitIdUseCase @Inject constructor (
    private val repository: TileRepository
){
    suspend operator fun invoke(kitId : Long) : List<Tile> = repository.getTilesByKitId(kitId)
}