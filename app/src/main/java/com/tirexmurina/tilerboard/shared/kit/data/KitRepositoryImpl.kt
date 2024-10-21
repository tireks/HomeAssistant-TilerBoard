package com.tirexmurina.tilerboard.shared.kit.data

import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.shared.kit.data.local.models.KitLocalDatabaseModel
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

class KitRepositoryImpl(
    private val userIdDataStore: UserIdDataStore,
    private val kitDao: KitDao,
    private val dispatcherIO: CoroutineDispatcher,
    private val converter: KitLocalDatabaseModelConverter
) : KitRepository {
    override suspend fun getKits(): List<Kit> {
        return withContext(dispatcherIO){
            val userId = userIdDataStore.userId ?: throw NullUserException("User id is Null")
            try {
                var kitsList = kitDao.getKitsByUserId(userId).map { converter.localModelToEntity(it) }
                if (kitsList.isEmpty()) {
                    createKit("Base Home Screen", R.drawable.ic_kit_icon_home)
                    kitsList = kitDao.getKitsByUserId(userId).map { converter.localModelToEntity(it) }
                }
                kitsList
            } catch ( exception : Exception){
                throw UserKitException("Cannot get kits for user")
            }
        }
    }

    override suspend fun createKit(name: String, iconResId: Int) {
        withContext(dispatcherIO){
            val userId = userIdDataStore.userId ?: throw NullUserException("User id is Null")
            try {
                val kitModel = KitLocalDatabaseModel(
                    linkedUserId = userId,
                    name = name,
                    iconResId = iconResId
                )
                kitDao.createKit(kitModel)
            } catch (exception : Exception){
                throw KitCreationException("Cannot create new kit")
            }
        }
    }

}