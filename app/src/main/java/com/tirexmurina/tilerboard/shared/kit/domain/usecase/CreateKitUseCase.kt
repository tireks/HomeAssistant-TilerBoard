package com.tirexmurina.tilerboard.shared.kit.domain.usecase

import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import javax.inject.Inject

class CreateKitUseCase @Inject constructor(
    private val repository: KitRepository
) {
    suspend operator fun invoke(name: String, iconResId: Int): Long = repository.createKit(name, iconResId)
}