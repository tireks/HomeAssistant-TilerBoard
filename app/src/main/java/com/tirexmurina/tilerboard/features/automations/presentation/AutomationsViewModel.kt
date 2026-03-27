package com.tirexmurina.tilerboard.features.automations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ActionType
import com.tirexmurina.tilerboard.shared.automation.domain.entity.Automation
import com.tirexmurina.tilerboard.shared.automation.domain.entity.AutomationAction
import com.tirexmurina.tilerboard.shared.automation.domain.entity.AutomationCondition
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ConditionType
import com.tirexmurina.tilerboard.shared.automation.domain.usecase.CreateAutomationUseCase
import com.tirexmurina.tilerboard.shared.automation.domain.usecase.DeleteAutomationUseCase
import com.tirexmurina.tilerboard.shared.automation.domain.usecase.ObserveAutomationsUseCase
import com.tirexmurina.tilerboard.shared.automation.domain.usecase.SetAutomationEnabledUseCase
import com.tirexmurina.tilerboard.shared.automation.service.AutomationServiceController
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetAllSensorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutomationsViewModel @Inject constructor(
    observeAutomationsUseCase: ObserveAutomationsUseCase,
    private val createAutomationUseCase: CreateAutomationUseCase,
    private val setAutomationEnabledUseCase: SetAutomationEnabledUseCase,
    private val deleteAutomationUseCase: DeleteAutomationUseCase,
    private val getAllSensorsUseCase: GetAllSensorsUseCase,
    private val serviceController: AutomationServiceController
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutomationsUiState())
    val uiState: StateFlow<AutomationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAutomationsUseCase().collect { automations ->
                _uiState.update { it.copy(automations = automations) }
            }
        }
        viewModelScope.launch {
            runCatching { getAllSensorsUseCase() }
                .onSuccess { sensors ->
                    _uiState.update {
                        it.copy(
                            sensors = sensors,
                            sourceEntityIdInput = it.sourceEntityIdInput.ifBlank {
                                sensors.firstOrNull()?.entityId.orEmpty()
                            }
                        )
                    }
                }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(nameInput = value) }

    fun onExpectedValueChanged(value: String) = _uiState.update { it.copy(expectedValueInput = value) }

    fun onPayloadChanged(value: String) = _uiState.update { it.copy(payloadInput = value) }

    fun onSourceEntityChanged(value: String) = _uiState.update { it.copy(sourceEntityIdInput = value) }

    fun onConditionTypeChanged(value: ConditionType) = _uiState.update { it.copy(conditionType = value) }

    fun onActionTypeChanged(value: ActionType) = _uiState.update {
        if (value == ActionType.WakeScreen) {
            it.copy(actionType = value, payloadInput = "")
        } else {
            it.copy(actionType = value)
        }
    }

    fun createAutomation() {
        val state = _uiState.value
        if (state.sourceEntityIdInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Выберите сенсор-источник") }
            return
        }

        viewModelScope.launch {
            val automation = Automation(
                id = 0,
                name = state.nameInput.ifBlank { "Automation ${System.currentTimeMillis()}" },
                sourceEntityId = state.sourceEntityIdInput,
                condition = AutomationCondition(state.conditionType, state.expectedValueInput),
                action = AutomationAction(
                    type = state.actionType,
                    payload = state.payloadInput.ifBlank { null }
                ),
                enabled = true
            )

            runCatching { createAutomationUseCase(automation) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            nameInput = "",
                            expectedValueInput = "",
                            payloadInput = "",
                            errorMessage = null
                        )
                    }
                    serviceController.syncServiceState()
                }
                .onFailure {
                    _uiState.update { old -> old.copy(errorMessage = it.message ?: "Не удалось создать автоматизацию") }
                }
        }
    }

    fun toggleAutomation(automationId: Long, enabled: Boolean) {
        viewModelScope.launch {
            setAutomationEnabledUseCase(automationId, enabled)
            serviceController.syncServiceState()
        }
    }

    fun deleteAutomation(automationId: Long) {
        viewModelScope.launch {
            deleteAutomationUseCase(automationId)
            serviceController.syncServiceState()
        }
    }

    fun runServiceSync() {
        serviceController.syncServiceState()
    }
}

data class AutomationsUiState(
    val automations: List<Automation> = emptyList(),
    val sensors: List<Sensor> = emptyList(),
    val nameInput: String = "",
    val sourceEntityIdInput: String = "",
    val expectedValueInput: String = "",
    val payloadInput: String = "",
    val conditionType: ConditionType = ConditionType.BinaryEquals,
    val actionType: ActionType = ActionType.WakeScreen,
    val errorMessage: String? = null
)
