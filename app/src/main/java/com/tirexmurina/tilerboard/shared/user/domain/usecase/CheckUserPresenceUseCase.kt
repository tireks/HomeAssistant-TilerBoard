package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import javax.inject.Inject

class CheckUserPresenceUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(login : String) : Boolean {
        val userId = repository.getId(login)
        return userId != null
    }
}