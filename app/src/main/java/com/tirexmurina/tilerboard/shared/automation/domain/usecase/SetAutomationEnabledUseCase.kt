package com.tirexmurina.tilerboard.shared.automation.domain.usecase

import com.tirexmurina.tilerboard.shared.automation.domain.repository.AutomationRepository
import javax.inject.Inject

class SetAutomationEnabledUseCase @Inject constructor(
    private val repository: AutomationRepository
) {
    suspend operator fun invoke(automationId: Long, enabled: Boolean) =
        repository.setAutomationEnabled(automationId, enabled)
}
