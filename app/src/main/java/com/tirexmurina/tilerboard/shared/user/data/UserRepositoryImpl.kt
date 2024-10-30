package com.tirexmurina.tilerboard.shared.user.data

import com.tirexmurina.tilerboard.shared.user.data.local.models.UserLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.user.data.local.source.TEST_TOKEN
import com.tirexmurina.tilerboard.shared.user.data.local.source.TokenDataStore
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserDao
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import com.tirexmurina.tilerboard.shared.user.util.DataBaseCorruptedException
import com.tirexmurina.tilerboard.shared.user.util.SharedPrefsCorruptedException
import com.tirexmurina.tilerboard.shared.user.util.TokenException
import com.tirexmurina.tilerboard.shared.user.util.UnknownException
import com.tirexmurina.tilerboard.shared.user.util.UserAccessLevel
import com.tirexmurina.tilerboard.shared.user.util.UserAuthException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val tokenDataStore: TokenDataStore,
    private val userIdDataStore : UserIdDataStore,
    private val dispatcherIO: CoroutineDispatcher
) : UserRepository{

    override suspend fun authUserLocal(login: String, accessLevel: UserAccessLevel) {
        withContext(dispatcherIO) {
            try {
                handleUserAuth(login, accessLevel)
                handleToken()
            } catch (exception: DataBaseCorruptedException) {
                handleExceptions(UserAuthException("Problem with user Auth"))
            } catch (exception: SharedPrefsCorruptedException) {
                handleExceptions(TokenException("Problem with token"))
            }
        }
    }

    private suspend fun handleUserAuth(login: String, accessLevel: UserAccessLevel) {
        try {
            val userPresence = userDao.isAnyUserPresent()
            if (!userPresence) {
                //todo все таки наверное стоит переписать по образу и подобию того как работает создание kit и tile
                val userModel = UserLocalDatabaseModel(login = login, access = accessLevel)
                userDao.createUser(userModel)
            }
            val userId = userDao.getUserId(login)
            userIdDataStore.userId = userId
        } catch (exception: Exception) {
            throw DataBaseCorruptedException("Problems with DB acquired.")
        }
    }

    private fun handleToken() {
        try {
            val tokenPresence = tokenDataStore.isAccessTokenSaved()
            if (!tokenPresence) {
                tokenDataStore.setAccessToken(TEST_TOKEN)
            }
            //TODO куда-то сюда еще должна встраиваться проверка на свежесть токена
        } catch (exception: Exception){
            throw SharedPrefsCorruptedException("Problems with SharedPrefs acquired")
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