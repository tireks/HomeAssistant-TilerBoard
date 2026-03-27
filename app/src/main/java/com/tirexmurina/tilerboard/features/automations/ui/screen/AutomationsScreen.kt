package com.tirexmurina.tilerboard.features.automations.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.automations.presentation.AutomationsUiState
import com.tirexmurina.tilerboard.features.automations.presentation.AutomationsViewModel
import com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen.SensorsListScreen
import com.tirexmurina.tilerboard.features.tileCreate.ui.OverlayContainer
import com.tirexmurina.tilerboard.features.util.cards.PlaceholderSimpleCard
import com.tirexmurina.tilerboard.features.util.cards.SensorCard
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ActionType
import com.tirexmurina.tilerboard.shared.automation.domain.entity.ConditionType

@Composable
fun AutomationsScreen(
    viewModel: AutomationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showSensorPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.runServiceSync()
    }

    AutomationsScreenContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onNameChanged = viewModel::onNameChanged,
        onExpectedValueChanged = viewModel::onExpectedValueChanged,
        onPayloadChanged = viewModel::onPayloadChanged,
        onConditionTypeChanged = viewModel::onConditionTypeChanged,
        onActionTypeChanged = viewModel::onActionTypeChanged,
        onCreateAutomation = viewModel::createAutomation,
        onToggleAutomation = viewModel::toggleAutomation,
        onDeleteAutomation = viewModel::deleteAutomation,
        onOpenSensorPicker = { showSensorPicker = true }
    )

    if (showSensorPicker) {
        OverlayContainer(onDismiss = { showSensorPicker = false }) {
            SensorsListScreen(
                onNavigateBack = { showSensorPicker = false },
                onSensorClick = {
                    viewModel.onSourceEntityChanged(it)
                    showSensorPicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutomationsScreenContent(
    state: AutomationsUiState,
    onNavigateBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onExpectedValueChanged: (String) -> Unit,
    onPayloadChanged: (String) -> Unit,
    onConditionTypeChanged: (ConditionType) -> Unit,
    onActionTypeChanged: (ActionType) -> Unit,
    onCreateAutomation: () -> Unit,
    onToggleAutomation: (Long, Boolean) -> Unit,
    onDeleteAutomation: (Long) -> Unit,
    onOpenSensorPicker: () -> Unit
) {
    val selectedSensor = state.sensors.firstOrNull { it.entityId == state.sourceEntityIdInput }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Автоматизация") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(id = R.drawable.ic_back), contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Новая автоматизация", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = state.nameInput,
                    onValueChange = onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название") }
                )

                if (selectedSensor == null) {
                    PlaceholderSimpleCard(
                        text = "Нажмите для выбора сенсора",
                        onClick = onOpenSensorPicker
                    )
                } else {
                    SensorCard(
                        sensor = selectedSensor,
                        onClick = { onOpenSensorPicker() }
                    )
                }

                var conditionExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = conditionExpanded,
                    onExpandedChange = { conditionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.conditionType.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тип правила") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = conditionExpanded,
                        onDismissRequest = { conditionExpanded = false }
                    ) {
                        ConditionType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName()) },
                                onClick = {
                                    onConditionTypeChanged(type)
                                    conditionExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = state.expectedValueInput,
                    onValueChange = onExpectedValueChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Значение правила (например: on или 30)") }
                )

                var actionExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = actionExpanded,
                    onExpandedChange = { actionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.actionType.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Действие") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actionExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = actionExpanded,
                        onDismissRequest = { actionExpanded = false }
                    ) {
                        ActionType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName()) },
                                onClick = {
                                    onActionTypeChanged(type)
                                    actionExpanded = false
                                }
                            )
                        }
                    }
                }

                if (state.actionType == ActionType.ShowAlertNotification) {
                    OutlinedTextField(
                        value = state.payloadInput,
                        onValueChange = onPayloadChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Текст алерта") }
                    )
                }

                state.errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Button(onClick = onCreateAutomation, modifier = Modifier.fillMaxWidth()) {
                    Text("Создать и запустить")
                }
            }

            item { HorizontalDivider() }
            item { Text("Существующие", style = MaterialTheme.typography.titleMedium) }

            items(state.automations, key = { it.id }) { automation ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = automation.name, style = MaterialTheme.typography.titleSmall)
                        Text(text = "Источник: ${automation.sourceEntityId}")
                        Text(text = "Условие: ${automation.condition.type.displayName()} = ${automation.condition.expectedValue}")
                        Text(text = "Действие: ${automation.action.type.displayName()}")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Включено")
                                Switch(
                                    checked = automation.enabled,
                                    onCheckedChange = { onToggleAutomation(automation.id, it) }
                                )
                            }
                            TextButton(onClick = { onDeleteAutomation(automation.id) }) {
                                Text("Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ConditionType.displayName(): String = when (this) {
    ConditionType.BinaryEquals -> "Равно (binary)"
    ConditionType.NumericGreaterThan -> "Больше чем"
    ConditionType.NumericLessThan -> "Меньше чем"
}

private fun ActionType.displayName(): String = when (this) {
    ActionType.WakeScreen -> "Включить экран"
    ActionType.ShowAlertNotification -> "Показать внутриприложный alert"
}
