package com.tirexmurina.tilerboard.shared.automation.domain.entity

data class Automation(
    val id: Long,
    val name: String,
    val sourceEntityId: String,
    val condition: AutomationCondition,
    val action: AutomationAction,
    val enabled: Boolean
)

enum class ConditionType {
    BinaryEquals,
    NumericGreaterThan,
    NumericLessThan
}

enum class ActionType {
    WakeScreen,
    ShowAlertNotification
}

data class AutomationCondition(
    val type: ConditionType,
    val expectedValue: String
)

data class AutomationAction(
    val type: ActionType,
    val payload: String?
)
