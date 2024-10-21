package com.tirexmurina.tilerboard.shared.kit.domain.usecase

import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository

class GetKitsUseCase (
    private val repository: KitRepository
){
    suspend operator fun invoke() : List<Kit> = repository.getKits()
}