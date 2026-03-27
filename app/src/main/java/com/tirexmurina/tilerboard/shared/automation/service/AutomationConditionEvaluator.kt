package com.tirexmurina.tilerboard.shared.automation.service

import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ConditionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutomationConditionEvaluator @Inject constructor() {

    fun shouldTrigger(automation: Automation, sensorState: String): Boolean {
        return when (automation.condition.type) {
            ConditionType.BinaryEquals -> {
                sensorState.equals(automation.condition.expectedValue, ignoreCase = true)
            }

            ConditionType.NumericGreaterThan -> {
                val current = sensorState.toDoubleOrNull() ?: return false
                val expected = automation.condition.expectedValue.toDoubleOrNull() ?: return false
                current > expected
            }

            ConditionType.NumericLessThan -> {
                val current = sensorState.toDoubleOrNull() ?: return false
                val expected = automation.condition.expectedValue.toDoubleOrNull() ?: return false
                current < expected
            }
        }
    }
}
