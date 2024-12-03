package com.tirexmurina.tilerboard.shared.kit.domain.usecase

import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import javax.inject.Inject

class GetKitsUseCase @Inject constructor (
    private val repository: KitRepository
){
    suspend operator fun invoke() : List<Kit> = repository.getKits()
}