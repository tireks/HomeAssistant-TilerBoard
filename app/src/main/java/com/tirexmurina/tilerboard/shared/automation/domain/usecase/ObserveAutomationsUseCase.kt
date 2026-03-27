package com.tirexmurina.tilerboard.shared.automation.domain.usecase

import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import com.tirexmurina.tilerboard.shared.automation.domain.repository.AutomationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAutomationsUseCase @Inject constructor(
    private val repository: AutomationRepository
) {
    operator fun invoke(): Flow<List<Automation>> = repository.observeAutomations()
}
