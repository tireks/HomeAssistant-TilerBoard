package com.tirexmurina.tilerboard.shared.tile.data

import com.tirexmurina.tilerboard.shared.datachange.DataChangeNotifier
import com.tirexmurina.tilerboard.shared.tile.data.local.source.KitTileLinkDao
import com.tirexmurina.tilerboard.shared.tile.domain.repository.KitTileLinkRepository
import com.tirexmurina.tilerboard.shared.tile.util.TileDetachException
import com.tirexmurina.tilerboard.shared.util.local.source.TileKitCrossRefLocalDatabaseModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KitTileLinkRepositoryImpl @Inject constructor(
    private val kitTileLinkDao: KitTileLinkDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val dataChangeNotifier: DataChangeNotifier
) : KitTileLinkRepository {

    override suspend fun linkTileToKit(tileId: Long, kitId: Long) {
        withContext(dispatcherIO) {
            kitTileLinkDao.linkTileToKit(
                TileKitCrossRefLocalDatabaseModel(
                    tileId = tileId,
                    kitId = kitId
                )
            )
            dataChangeNotifier.notifyChanged()
        }
    }

    override suspend fun detachTileFromKit(tileId: Long, kitId: Long) {
        withContext(dispatcherIO) {
            try {
                kitTileLinkDao.unlinkTileFromKit(tileId, kitId)
                dataChangeNotifier.notifyChanged()
            } catch (exception: Exception) {
                throw TileDetachException(exception.message.toString())
            }
        }
    }

    override suspend fun clearTileLinks(tileId: Long) {
        withContext(dispatcherIO) {
            kitTileLinkDao.clearTileLinks(tileId)
        }
    }
}
