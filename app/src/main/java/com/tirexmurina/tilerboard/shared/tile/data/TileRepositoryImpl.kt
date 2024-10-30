package com.tirexmurina.tilerboard.shared.tile.data

import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelConverter
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.util.BinaryEnumOnOff
import com.tirexmurina.tilerboard.shared.tile.util.KitTileException
import com.tirexmurina.tilerboard.shared.tile.util.TileCreationException
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor (
    private val tileDao: TileDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val converter: TileLocalDatabaseModelConverter
) : TileRepository{
    override suspend fun getTilesByKitId(kitId: Long): List<Tile> {
        return withContext(dispatcherIO){
            try {
                if (tileDao.getTilesCountByUserId(kitId) == 0){
                    createTile(
                        BinarySimpleOnOff(BinaryEnumOnOff.ON),
                        kitId
                    )
                }
                val tilesList = tileDao.getTilesByUserId(kitId).map { converter.localModelToEntity(it) }
                tilesList
            } catch (exception : Exception){
                throw KitTileException("Cannot get tiles for that kit")
            }
        }
    }

    override suspend fun createTile(type: TileType, kitId: Long) {
        return withContext(dispatcherIO){
            try {
                val tile = buildTile(type)
                tileDao.createTile(converter.entityToLocalModel(tile, kitId))
            } catch ( exception : Exception){
                throw TileCreationException("Problems with tile creation")
            }
        }
    }

    private fun buildTile(type: TileType) =
        Tile(
            id = 0,
            type = type
        )

}