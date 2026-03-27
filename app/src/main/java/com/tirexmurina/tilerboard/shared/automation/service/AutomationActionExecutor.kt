package com.tirexmurina.tilerboard.shared.automation.service

import android.content.Context
import android.os.PowerManager
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ActionType
import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutomationActionExecutor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun execute(automation: Automation, currentSensorState: String) {
        when (automation.action.type) {
            ActionType.WakeScreen -> wakeScreen()
            ActionType.ShowAlertNotification -> showInAppAlert(automation, currentSensorState)
        }
    }

    private fun wakeScreen() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isInteractive) return

        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "tilerboard:automationWakeLock"
        )
        wakeLock.acquire(3000)
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun showInAppAlert(automation: Automation, currentSensorState: String) {
        val text = automation.action.payload
            ?.takeIf { it.isNotBlank() }
            ?: "${automation.sourceEntityId}: $currentSensorState"

        AutomationInAppAlertCenter.postAlert(
            AutomationInAppAlert(
                title = "Automation: ${automation.name}",
                message = text,
                automationId = automation.id
            )
        )
    }
}
