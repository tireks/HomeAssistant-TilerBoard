package com.tirexmurina.tilerboard.shared.kit.domain.usecase

import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import javax.inject.Inject

class UserHasKitsUseCase @Inject constructor(
    private val repository: KitRepository
) {
    suspend operator fun invoke(): Boolean {
        val userKitsCount = repository.getKitsNumber()
        return userKitsCount != 0
    }
}