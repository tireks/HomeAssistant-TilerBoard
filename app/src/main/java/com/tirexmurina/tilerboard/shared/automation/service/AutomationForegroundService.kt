package com.tirexmurina.tilerboard.shared.automation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.shared.automation.domain.usecase.GetEnabledAutomationsUseCase
import com.tirexmurina.tilerboard.shared.kit.util.NullUserException
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetSensorDataByIdUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AutomationForegroundService : Service() {

    @Inject
    lateinit var getEnabledAutomationsUseCase: GetEnabledAutomationsUseCase

    @Inject
    lateinit var getSensorDataByIdUseCase: GetSensorDataByIdUseCase

    @Inject
    lateinit var conditionEvaluator: AutomationConditionEvaluator

    @Inject
    lateinit var actionExecutor: AutomationActionExecutor

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val triggerStateMap = mutableMapOf<Long, Boolean>()

    override fun onCreate() {
        super.onCreate()
        startForeground(SERVICE_NOTIFICATION_ID, buildPersistentNotification())
        startLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLoop() {
        serviceScope.launch {
            while (isActive) {
                try {
                    val enabledAutomations = getEnabledAutomationsUseCase()
                    if (enabledAutomations.isEmpty()) {
                        stopSelf()
                        break
                    }

                    enabledAutomations.forEach { automation ->
                        val sensor = getSensorDataByIdUseCase(automation.sourceEntityId)
                        val shouldTriggerNow = conditionEvaluator.shouldTrigger(automation, sensor.state)
                        val wasTriggeredBefore = triggerStateMap[automation.id] ?: false

                        if (shouldTriggerNow && !wasTriggeredBefore) {
                            actionExecutor.execute(automation, sensor.state)
                        }

                        triggerStateMap[automation.id] = shouldTriggerNow
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: NullUserException) {
                    stopSelf()
                    break
                } catch (_: Exception) {
                    // Не останавливаем сервис на случай временных сетевых ошибок.
                }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun buildPersistentNotification(): Notification {
        ensureServiceChannel()
        return NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle("Automatization is running")
            .setContentText("Rules are checked in background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    private fun ensureServiceChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            SERVICE_CHANNEL_ID,
            "Automation service",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val SERVICE_CHANNEL_ID = "automation_service"
        private const val SERVICE_NOTIFICATION_ID = 4021
        private const val POLL_INTERVAL_MS = 3000L

        fun start(context: Context) {
            val intent = Intent(context, AutomationForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AutomationForegroundService::class.java))
        }
    }
}
