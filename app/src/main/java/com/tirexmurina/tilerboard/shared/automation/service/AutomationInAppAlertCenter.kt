package com.tirexmurina.tilerboard.shared.automation.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayDeque

/**
 * Глобальный центр in-app алертов.
 * Алерт всегда показывается только один; остальные попадают в очередь.
 */
object AutomationInAppAlertCenter {

    private val pendingAlerts = ArrayDeque<AutomationInAppAlert>()
    private val _currentAlert = MutableStateFlow<AutomationInAppAlert?>(null)
    val currentAlert: StateFlow<AutomationInAppAlert?> = _currentAlert.asStateFlow()

    fun postAlert(alert: AutomationInAppAlert) {
        synchronized(this) {
            if (_currentAlert.value == null) {
                _currentAlert.value = alert
            } else {
                pendingAlerts.addLast(alert)
            }
        }
    }

    fun dismissCurrentAlert() {
        synchronized(this) {
            _currentAlert.value = if (pendingAlerts.isEmpty()) {
                null
            } else {
                pendingAlerts.removeFirst()
            }
        }
    }
}

data class AutomationInAppAlert(
    val title: String,
    val message: String,
    val automationId: Long,
    val createdAtMs: Long = System.currentTimeMillis()
)
