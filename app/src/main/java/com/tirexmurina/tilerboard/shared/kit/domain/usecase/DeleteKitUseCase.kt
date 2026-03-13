package com.tirexmurina.tilerboard.shared.kit.domain.usecase

import com.tirexmurina.tilerboard.shared.kit.domain.repository.KitRepository
import javax.inject.Inject

class DeleteKitUseCase @Inject constructor(
    private val repository: KitRepository
) {
    suspend operator fun invoke(kitId: Long) = repository.deleteKit(kitId)
}
