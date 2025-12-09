package com.tirexmurina.tilerboard.shared.kit.data

import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.shared.kit.data.local.models.converter.KitLocalDatabaseModelConverter
import com.tirexmurina.tilerboard.shared.kit.data.local.source.KitDao
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import com.tirexmurina.tilerboard.shared.kit.util.KitCreationException
import com.tirexmurina.tilerboard.shared.kit.util.NullUserException
import com.tirexmurina.tilerboard.shared.kit.util.UserKitException
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KitRepositoryImpl @Inject constructor(
    private val userIdDataStore: UserIdDataStore,
    private val kitDao: KitDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val converter: KitLocalDatabaseModelConverter
) : KitRepository {
    override suspend fun getKits(): List<Kit> {
        return withContext(dispatcherIO){
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            try {
                if (kitDao.getKitCountByUserId(userId) == 0){
                    createKit("Base Home Screen", R.drawable.ic_kit_icon_home)
                }
                val kitsList = kitDao.getKitsByUserId(userId).map { converter.localModelToEntity(it) }
                kitsList
            } catch ( exception : Exception){
                throw UserKitException("Cannot get kits for user. " + exception.message.toString())
            }
        }
    }

    override suspend fun createKit(name: String, iconResId: Int) {
        withContext(dispatcherIO){
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            try {
                val kit = buildKit(name, iconResId)
                kitDao.createKit(converter.entityToLocalModel(kit, userId))
            } catch (exception : Exception){
                throw KitCreationException("Cannot create new kit. " + exception.message.toString())
            }
        }
    }

    override suspend fun getKitsNumber(): Int {
        return withContext(dispatcherIO){
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            kitDao.getKitCountByUserId(userId)
        }
    }

    private fun buildKit(name: String, iconResId: Int) =
        Kit(
            id = 0,
            name,
            iconResId
        )

}