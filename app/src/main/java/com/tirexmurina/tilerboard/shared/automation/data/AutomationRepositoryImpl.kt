package com.tirexmurina.tilerboard.shared.automation.data

import com.tirexmurina.tilerboard.shared.automation.data.local.models.AutomationLocalDatabaseModel
import com.tirexmurina.tilerboard.shared.automation.data.local.source.AutomationDao
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ActionType
import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import com.tirexmurina.tilerboard.shared.automation.domain.entity.AutomationAction
import com.tirexmurina.tilerboard.shared.automation.domain.entity.AutomationCondition
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ConditionType
import com.tirexmurina.tilerboard.shared.automation.domain.repository.AutomationRepository
import com.tirexmurina.tilerboard.shared.kit.util.NullUserException
import com.tirexmurina.tilerboard.shared.user.data.local.source.UserIdDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AutomationRepositoryImpl @Inject constructor(
    private val automationDao: AutomationDao,
    private val userIdDataStore: UserIdDataStore,
    private val dispatcherIO: CoroutineDispatcher
) : AutomationRepository {

    override fun observeAutomations(): Flow<List<Automation>> {
        val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
        return automationDao.observeAll(userId).map { automations -> automations.map { it.toDomain() } }
    }

    override suspend fun getEnabledAutomations(): List<Automation> {
        return withContext(dispatcherIO) {
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            automationDao.getEnabled(userId).map { it.toDomain() }
        }
    }

    override suspend fun createAutomation(automation: Automation): Long {
        return withContext(dispatcherIO) {
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            automationDao.create(automation.toLocalModel(userId))
        }
    }

    override suspend fun setAutomationEnabled(automationId: Long, enabled: Boolean) {
        withContext(dispatcherIO) {
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            automationDao.setEnabled(automationId, enabled, userId)
        }
    }

    override suspend fun deleteAutomation(automationId: Long) {
        withContext(dispatcherIO) {
            val userId = userIdDataStore.get() ?: throw NullUserException("User id is Null")
            automationDao.deleteById(automationId, userId)
        }
    }


    private fun AutomationLocalDatabaseModel.toDomain(): Automation {
        return Automation(
            id = id,
            name = name,
            sourceEntityId = sourceEntityId,
            condition = AutomationCondition(
                type = ConditionType.valueOf(conditionType),
                expectedValue = conditionExpectedValue
            ),
            action = AutomationAction(
                type = ActionType.valueOf(actionType),
                payload = actionPayload
            ),
            enabled = enabled
        )
    }

    private fun Automation.toLocalModel(userId: Long): AutomationLocalDatabaseModel {
        return AutomationLocalDatabaseModel(
            id = id,
            linkedUserId = userId,
            name = name,
            sourceEntityId = sourceEntityId,
            conditionType = condition.type.name,
            conditionExpectedValue = condition.expectedValue,
            actionType = action.type.name,
            actionPayload = action.payload,
            enabled = enabled
        )
    }
}
