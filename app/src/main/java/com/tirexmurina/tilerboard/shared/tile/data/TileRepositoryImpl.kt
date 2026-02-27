package com.tirexmurina.tilerboard.shared.tile.data

import android.util.Log
import com.tirexmurina.tilerboard.shared.sensor.domain.repository.SensorRepository
import com.tirexmurina.tilerboard.shared.tile.data.local.models.TileSensorlessDTO
import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelHelper
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.util.KitTileException
import com.tirexmurina.tilerboard.shared.tile.util.TileCreationException
import com.tirexmurina.tilerboard.shared.tile.util.TileDetachException
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.util.local.source.TileKitCrossRefLocalDatabaseModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor(
    private val tileDao: TileDao,
    private val sensorRepository: SensorRepository,
    private val dispatcherIO: CoroutineDispatcher,
    private val localDatabaseModelHelper: TileLocalDatabaseModelHelper
) : TileRepository {

    override suspend fun getTilesByKitId(kitId: Long): List<TileSensorlessDTO> {
        return withContext(dispatcherIO) {
            try {
                val kitWithTiles = tileDao.getKitWithTilesByKitId(kitId)
                    ?: throw KitTileException("Kit with id=$kitId was not found")
                kitWithTiles.tiles.map {
                    localDatabaseModelHelper.fromLocalModel(it)
                }
            } catch (exception: Exception) {
                Log.e("EXCEPTIONSAS", "Ошибка при получении тайлов: ${exception.message}", exception)
                throw KitTileException("Cannot get tiles for that kit: ${exception.message}")
            }
        }
    }

    override suspend fun getAllTiles(): List<Tile> {
        return withContext(dispatcherIO) {
            tileDao.getAllTiles().map { localModel ->
                val sensor = sensorRepository.getSensorDataByNameId(localModel.linkedSensorEntityId)
                Tile(
                    id = localModel.id,
                    type = localDatabaseModelHelper.fromLocalModel(localModel).type,
                    name = localModel.name,
                    sensor = sensor
                )
            }
        }
    }

    override suspend fun getTileById(tileId: Long): Tile {
        return withContext(dispatcherIO) {
            val localModel = tileDao.getTileById(tileId)
                ?: throw KitTileException("Tile with id=$tileId not found")
            val sensor = sensorRepository.getSensorDataByNameId(localModel.linkedSensorEntityId)
            Tile(
                id = localModel.id,
                type = localDatabaseModelHelper.fromLocalModel(localModel).type,
                name = localModel.name,
                sensor = sensor
            )
        }
    }

    override suspend fun createTile(type: TileType, linkedSensorId: String, name: String?): Long {
        return withContext(dispatcherIO) {
            try {
                val tileDBModel = localDatabaseModelHelper.buildTileDbModel(type, linkedSensorId, name)
                tileDao.createTile(tileDBModel)
            } catch (exception: Exception) {
                throw TileCreationException(exception.message.toString())
            }
        }
    }

    override suspend fun linkTileToKit(tileId: Long, kitId: Long) {
        withContext(dispatcherIO) {
            tileDao.linkTileToKit(
                TileKitCrossRefLocalDatabaseModel(
                    tileId = tileId,
                    kitId = kitId
                )
            )
        }
    }

    override suspend fun detachTileFromKit(tileId: Long, kitId: Long) {
        return withContext(dispatcherIO) {
            try {
                tileDao.unlinkTileFromKit(tileId, kitId)
                tileDao.deleteTileIfOrphan(tileId)
            } catch (exception: Exception) {
                throw TileDetachException(exception.message.toString())
            }
        }
    }
}
