package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import com.tirexmurina.tilerboard.shared.user.util.UnknownException
import com.tirexmurina.tilerboard.shared.user.util.UserAuthException
import com.tirexmurina.tilerboard.shared.util.remote.source.TokenCorruptedOrUnavailable
import com.tirexmurina.tilerboard.source.remote.TokenDataStore
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val userIdDataStore : UserIdDataStore,
    private val tokenDataStore: TokenDataStore
) {
    suspend operator fun invoke(newLogin: String) {
        try {
            if (!tokenDataStore.isAccessTokenSaved()) throw TokenCorruptedOrUnavailable("Token not found")
            val newUserId = repository.create(newLogin)
            if (newUserId == null) throw UserAuthException("Failed to create user")
            userIdDataStore.set(newUserId)
        } catch (e : Exception) {
            when (e) {
                is UserAuthException, is TokenCorruptedOrUnavailable -> throw e
                is CancellationException -> Unit
                else -> throw UnknownException(e.message.toString())
            }
        }
    }
}