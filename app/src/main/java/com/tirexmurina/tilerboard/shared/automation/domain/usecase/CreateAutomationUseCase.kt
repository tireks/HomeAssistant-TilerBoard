package com.tirexmurina.tilerboard.shared.automation.domain.usecase

import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import com.tirexmurina.tilerboard.shared.automation.domain.repository.AutomationRepository
import javax.inject.Inject

class CreateAutomationUseCase @Inject constructor(
    private val repository: AutomationRepository
) {
    suspend operator fun invoke(automation: Automation): Long = repository.createAutomation(automation)
}
