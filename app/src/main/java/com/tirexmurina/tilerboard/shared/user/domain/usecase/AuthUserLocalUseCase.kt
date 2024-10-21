package com.tirexmurina.tilerboard.shared.user.domain.usecase

import com.tirexmurina.tilerboard.shared.user.domain.repository.UserRepository
import com.tirexmurina.tilerboard.shared.user.util.UserAccessLevel

class AuthUserLocalUseCase (
    private val repository: UserRepository
){
    suspend operator fun invoke(login : String, userAccessLevel: UserAccessLevel) = repository.authUserLocal(login, userAccessLevel)
}