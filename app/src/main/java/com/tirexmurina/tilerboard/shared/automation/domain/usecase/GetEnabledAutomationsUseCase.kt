package com.tirexmurina.tilerboard.shared.automation.domain.usecase

import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import com.tirexmurina.tilerboard.shared.automation.domain.repository.AutomationRepository
import javax.inject.Inject

class GetEnabledAutomationsUseCase @Inject constructor(
    private val repository: AutomationRepository
) {
    suspend operator fun invoke(): List<Automation> = repository.getEnabledAutomations()
}
