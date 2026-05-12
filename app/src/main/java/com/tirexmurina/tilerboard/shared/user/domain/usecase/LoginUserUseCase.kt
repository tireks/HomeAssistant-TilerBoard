package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import com.tirexmurina.tilerboard.shared.user.domain.repository.TokenRepository
import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import com.tirexmurina.tilerboard.shared.user.util.UnknownException
import com.tirexmurina.tilerboard.shared.user.util.UserAuthException
import com.tirexmurina.tilerboard.shared.util.remote.source.TokenCorruptedOrUnavailable
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val userIdDataStore : UserIdDataStore,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(login : String) {
        try {
            if (!tokenRepository.isAccessTokenSaved()) throw TokenCorruptedOrUnavailable("Token not found")
            val userId = repository.getId(login)
            if (userId == null) throw UserAuthException("User not found")

            userIdDataStore.set(userId)
        } catch (e: Exception) {
            when (e) {
                is UserAuthException, is TokenCorruptedOrUnavailable -> throw e
                is CancellationException -> Unit
                else -> throw UnknownException(e.message.toString())
            }
        }
    }
}