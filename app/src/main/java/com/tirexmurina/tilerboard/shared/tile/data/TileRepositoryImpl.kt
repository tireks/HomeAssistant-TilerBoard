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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor (
    private val tileDao: TileDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val localDatabaseModelHelper: TileLocalDatabaseModelHelper
) : TileRepository{

    /*private var localTileValue = ON*/
    /*override suspend fun getTilesFlowByKitId(kitId: Long): Flow<List<Tile>> {
        val basicTilesList : List<Tile> = getTilesByKitId(kitId)
        val tilesFlow = flow {
            while (true) {
                val tilesList = basicTilesList.map { testTileProvider(it) }
                emit(tilesList)
                delay(3000L)
            }
        }
        return tilesFlow
    }*/

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
                    localDatabaseModelHelper.fromLocalModel(
                        it,
                        tileDao.getSimpleSwitchOnOffByTileId(it.id)
                    )
                }
                tilesList
            } catch (exception : Exception){
                Log.e("EXCEPTIONSAS", "Ошибка при получении тайлов: ${exception.message}", exception)
                throw KitTileException("Cannot get tiles for that kit: ${exception.message}")
            }
        }
    }

    /*fun testTileProvider(tile: Tile): Tile {
        //todo говнокод и порнография, придется пересоздавать объекты каждые три секунды,
        // вероятно придется юзать датаклассы с var чтобы менять напрямую "по ссылке"
        if (tile.type is SimpleBinaryOnOff) {
            if (localTileValue == ON) {
                Log.d("EXCEPTIONSAS", "doing OFF")
                localTileValue = OFF
                return Tile(
                    id = tile.id,
                    type = SimpleBinaryOnOff(OFF)
                )
            } else {
                Log.d("EXCEPTIONSAS", "doing ON")
                localTileValue = ON
                return Tile(
                    id = tile.id,
                    type = SimpleBinaryOnOff(ON)
                )
            }
        } else return tile
    }*/

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