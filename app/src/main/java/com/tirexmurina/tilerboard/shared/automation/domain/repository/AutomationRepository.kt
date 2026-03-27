package com.tirexmurina.tilerboard.shared.automation.domain.repository

import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import kotlinx.coroutines.flow.Flow

interface AutomationRepository {
    fun observeAutomations(): Flow<List<Automation>>

    suspend fun getEnabledAutomations(): List<Automation>

    suspend fun createAutomation(automation: Automation): Long

    suspend fun setAutomationEnabled(automationId: Long, enabled: Boolean)

    suspend fun deleteAutomation(automationId: Long)
}
