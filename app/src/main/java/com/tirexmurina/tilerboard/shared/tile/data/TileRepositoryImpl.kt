package com.tirexmurina.tilerboard.shared.tile.data

import android.util.Log
import com.tirexmurina.tilerboard.shared.tile.data.local.models.SimpleSwitchOnOffDatabaseModel
import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelHelper
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.util.KitTileException
import com.tirexmurina.tilerboard.shared.tile.util.TileCreationException
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleHumidity
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleTemperature
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_BINARY_ON_OFF
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_HUMIDITY
import com.tirexmurina.tilerboard.shared.tile.util.TileTypeEnum.SIMPLE_TEMPERATURE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor (
    private val tileDao: TileDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val localDatabaseModelHelper: TileLocalDatabaseModelHelper
) : TileRepository{

    override suspend fun getTilesByKitId(kitId: Long): List<Tile> {
        return withContext(dispatcherIO){
            try {
                if (tileDao.getTilesCountByUserId(kitId) == 0){
                    createTile(
                        SimpleBinaryOnOff(null),
                        kitId
                    )
                }
                val tilesList = tileDao.getTilesByUserId(kitId).map {
                    when(it.type){
                        SIMPLE_TEMPERATURE -> TODO()
                        SIMPLE_HUMIDITY -> TODO()
                        SIMPLE_BINARY_ON_OFF -> {
                            localDatabaseModelHelper.fromLocalModel(
                                it,
                                tileDao.getSimpleSwitchOnOffByTileId(it.id)
                            )
                        }
                    }

                }
                tilesList
            } catch (exception : Exception){
                Log.e("EXCEPTIONSAS", "Ошибка при получении тайлов: ${exception.message}", exception)
                throw KitTileException("Cannot get tiles for that kit: ${exception.message}")
            }
        }
    }

    override suspend fun createTile(type: TileType, kitId: Long) {
        return withContext(dispatcherIO){
            try {
                val tile = localDatabaseModelHelper.buildTile(type)
                val convertedTile = localDatabaseModelHelper.toLocalModel(tile, kitId)
                val tileId = tileDao.createTile(convertedTile)
                when(type){
                    is SimpleBinaryOnOff -> tileDao.createSimpleSwitchOnOff(
                        SimpleSwitchOnOffDatabaseModel(
                            linkedTileId = tileId,
                            state = type.state
                        )
                    )
                    is SimpleHumidity -> TODO()
                    is SimpleTemperature -> TODO()
                }
            } catch ( exception : Exception){
                throw TileCreationException("Problems with tile creation")
            }
        }
    }

}