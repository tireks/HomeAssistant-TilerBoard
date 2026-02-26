package com.tirexmurina.tilerboard.shared.tile.data

import android.util.Log
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileSensorlessDTO
import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelHelper
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.util.KitTileException
import com.tirexmurina.tilerboard.shared.tile.util.TileCreationException
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.util.local.source.TileKitCrossRefLocalDatabaseModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor (
    private val tileDao: TileDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val localDatabaseModelHelper: TileLocalDatabaseModelHelper
) : TileRepository{

    override suspend fun getTilesByKitId(kitId: Long): List<TileSensorlessDTO> {
        return withContext(dispatcherIO){
            try {
                //todo вот это ерунда, когда будет экран создания тайлов, нужен будет такой возврат(эксепшн),
                // чтобы в месте, куда возврат идет - перенаравлять на экран создания тайлов
                if (tileDao.getTileLinksCountByKitId(kitId) == 0){
                    createTile(
                        TileType.SimpleTemperature(null),
                        kitId,
                        "sensor.temp_1",
                        null
                    )
                }
                val kitWithTiles = tileDao.getKitWithTilesByKitId(kitId)
                    ?: throw KitTileException("Kit with id=$kitId was not found")
                kitWithTiles.tiles.map {
                    localDatabaseModelHelper.fromLocalModel(it)
                }
            } catch (exception : Exception){
                Log.e("EXCEPTIONSAS", "Ошибка при получении тайлов: ${exception.message}", exception)
                throw KitTileException("Cannot get tiles for that kit: ${exception.message}")
            }
        }
    }

    override suspend fun createTile(type: TileType, kitId: Long, linkedSensorId: String, name: String?) {
        return withContext(dispatcherIO){
            try {
                val tileDBModel = localDatabaseModelHelper.buildTileDbModel(type, linkedSensorId, name)
                val tileId = tileDao.createTile(tileDBModel)
                tileDao.linkTileToKit(
                    TileKitCrossRefLocalDatabaseModel(
                        tileId = tileId,
                        kitId = kitId
                    )
                )
            } catch ( exception : Exception){
                throw TileCreationException(exception.message.toString())
            }
        }
    }

}