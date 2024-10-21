package com.tirexmurina.tilerboard.shared.tile.domain.usecase

import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository

class GetTilesByKitIdUseCase (
    private val repository: TileRepository
){
    suspend operator fun invoke(kitId : Long) : List<Tile> = repository.getTilesByKitId(kitId)
}