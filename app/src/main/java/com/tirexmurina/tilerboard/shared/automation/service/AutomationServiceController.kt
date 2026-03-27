package com.tirexmurina.tilerboard.shared.automation.service

import android.content.Context
import com.tirexmurina.tilerboard.shared.automation.domain.usecase.GetEnabledAutomationsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutomationServiceController @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val getEnabledAutomationsUseCase: GetEnabledAutomationsUseCase,
    private val dispatcherIO: CoroutineDispatcher
) {

    fun syncServiceState() {
        CoroutineScope(dispatcherIO).launch {
            runCatching { getEnabledAutomationsUseCase() }
                .onSuccess { enabledAutomations ->
                    if (enabledAutomations.isEmpty()) {
                        AutomationForegroundService.stop(appContext)
                    } else {
                        AutomationForegroundService.start(appContext)
                    }
                }
                .onFailure {
                    AutomationForegroundService.stop(appContext)
                }
        }
    }
}
