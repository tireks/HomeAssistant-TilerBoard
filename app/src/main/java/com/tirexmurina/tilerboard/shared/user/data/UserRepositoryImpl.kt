package com.tirexmurina.tilerboard.shared.user.data

import com.tirexmurina.tilerboard.shared.user.data.local.models.UserLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserDao
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import com.tirexmurina.tilerboard.shared.user.data.remote.source.UserApi
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import com.tirexmurina.tilerboard.shared.user.util.DataBaseCorruptedException
import com.tirexmurina.tilerboard.shared.user.util.SharedPrefsCorruptedException
import com.tirexmurina.tilerboard.shared.user.util.TokenException
import com.tirexmurina.tilerboard.shared.user.util.UnknownException
import com.tirexmurina.tilerboard.shared.user.util.UserAccessLevel
import com.tirexmurina.tilerboard.shared.user.util.UserAuthException
import com.tirexmurina.tilerboard.shared.util.remote.source.RequestFault
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userIdDataStore : UserIdDataStore,
    private val dispatcherIO: CoroutineDispatcher,
    private val userApi: UserApi
) : UserRepository{
    override suspend fun getId(login: String) : Long? {
        try {
            return userDao.getUserId(login)
        } catch (exception : Exception) {
            handleExceptions(UserAuthException(exception.message.toString()))
        }
    }

    override suspend fun create(login: String): Long? {
        try {
            val userModel = UserLocalDatabaseModel(login = login)
            return userDao.createUser(userModel)
        } catch (exception : Exception) {
            handleExceptions(UserAuthException(exception.message.toString()))
        }
    }

    override suspend fun isApiAvailable(): Boolean {
        try {
            val response = userApi.getAvailability()
            if (response.isSuccessful) {
                return response.body()?.message == "API running."
            }
            return false
        } catch ( e : Exception) {
            throw RequestFault("Api availability request failed: " + e.message.toString())
        }
    }

    private fun handleExceptions(exception: Exception): Nothing {
        when (exception) {
            is UserAuthException -> {
                throw exception
            }
            is TokenException -> {
                throw exception
            }
            else -> {
                throw UnknownException("Some problems acquired")
            }
        }
    }


}