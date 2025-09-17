package com.tirexmurina.tilerboard.shared.tile.data

import android.util.Log
import com.tirexmurina.tilerboard.shared.tile.data.local.models.converter.TileLocalDatabaseModelHelper
import com.tirexmurina.tilerboard.shared.tile.data.local.source.TileDao
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.repository.TileRepository
import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum
import com.tirexmurina.tilerboard.shared.tile.util.KitTileException
import com.tirexmurina.tilerboard.shared.tile.util.TileCreationException
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
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
                //todo вот это ерунда, когда будет экран создания тайлов, нужен будет такой возврат(эксепшн),
                // чтобы в месте, куда возврат идет - перенаравлять на экран создания тайлов
                if (tileDao.getTilesCountByKitId(kitId) == 0){
                    createTile(
                        SimpleBinaryOnOff(BinaryOnOffEnum.ON),
                        kitId
                    )
                }
                val tilesList = tileDao.getTilesByKitId(kitId).map {
                    localDatabaseModelHelper.fromLocalModel(it)
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
            } catch ( exception : Exception){
                throw TileCreationException("Problems with tile creation")
            }
        }
    }

}