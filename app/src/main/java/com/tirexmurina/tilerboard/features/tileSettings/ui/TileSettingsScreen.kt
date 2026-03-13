package com.tirexmurina.tilerboard.features.tileSettings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen.SensorsListScreen
import com.tirexmurina.tilerboard.features.tileCreate.ui.OverlayContainer
import com.tirexmurina.tilerboard.features.tileCreate.ui.composables.SimpleIconSelectorLocked
import com.tirexmurina.tilerboard.features.tileSettings.presentation.TileSettingsViewModel
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.cards.PlaceholderSimpleCard
import com.tirexmurina.tilerboard.features.util.cards.SensorCard
import com.tirexmurina.tilerboard.features.util.tileCards.SimpleBinaryTileCard
import com.tirexmurina.tilerboard.features.util.tileCards.SimpleHumiditySensorTileCard
import com.tirexmurina.tilerboard.features.util.tileCards.SimpleNoTypeTileCard
import com.tirexmurina.tilerboard.features.util.tileCards.SimpleTemperatureSensorTileCard
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType

@Composable
fun TileSettingsScreen(
    viewModel: TileSettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showSensorPicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                TileSettingsViewModel.TileSettingsEvent.NavigateBack -> onNavigateBack()
                is TileSettingsViewModel.TileSettingsEvent.ShowError -> errorMessage = event.message
            }
        }
    }

    errorMessage?.let {
        SingleButtonDialog(title = "Произошла ошибка", message = it, onDismiss = { errorMessage = null })
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удаление тайла") },
            text = { Text("Вы уверены, что хотите удалить тайл") },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") } },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteTile()
                }) { Text("Да") }
            }
        )
    }

    when (val uiState = state) {
        TileSettingsViewModel.TileSettingsState.Loading -> LoadingScreen()
        is TileSettingsViewModel.TileSettingsState.Content -> {
            TileSettingsContent(
                tile = uiState.tile,
                tileName = uiState.tileName,
                canTest = uiState.canTest,
                canSave = uiState.canSave,
                onNavigateBack = onNavigateBack,
                onNameChanged = viewModel::updateName,
                onSelectSensorClick = { showSensorPicker = true },
                onDelete = { showDeleteDialog = true },
                onTest = viewModel::spawnPreview,
                onSave = viewModel::saveTile
            )

            if (showSensorPicker) {
                OverlayContainer(onDismiss = { showSensorPicker = false }) {
                    SensorsListScreen(
                        onNavigateBack = { showSensorPicker = false },
                        onSensorClick = {
                            viewModel.selectSensor(it)
                            showSensorPicker = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TileSettingsContent(
    tile: Tile,
    tileName: String,
    canTest: Boolean,
    canSave: Boolean,
    onNavigateBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSelectSensorClick: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Настройка тайла") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
                }
            }
        )
        Row(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(0.4f)) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        SimpleIconSelectorLocked(
                            hintText = "Нажмите чтобы выбрать иконку",
                            dialogTitle = "Выбор иконки",
                            dialogMessage = "Извините, пока доступна только эта иконка"
                        )
                    }
                    TextField(
                        value = tileName,
                        onValueChange = onNameChanged,
                        label = { Text("Название тайла") },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                    if (tile.sensor.entityId.isBlank()) {
                        PlaceholderSimpleCard(text = "Нажмите для выбора сенсора", onClick = onSelectSensorClick)
                    } else {
                        SensorCard(sensor = tile.sensor, onClick = { onSelectSensorClick() })
                    }
                }
                Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) { Text("Удалить тайл") }
                Button(onClick = onTest, modifier = Modifier.fillMaxWidth(), enabled = canTest) { Text("Проверить тайл") }
                Button(onClick = onSave, modifier = Modifier.fillMaxWidth(), enabled = canSave) { Text("Сохранить тайл") }
            }
            Box(modifier = Modifier.weight(0.6f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                TilePreview(tile)
            }
        }
    }
}

@Composable
private fun TilePreview(tile: Tile) {
    when (val type = tile.type) {
        is TileType.SimpleTemperature -> SimpleTemperatureSensorTileCard(title = tile.name ?: tile.sensor.friendlyName, state = type.temperature)
        is TileType.SimpleBinaryOnOff -> SimpleBinaryTileCard(title = tile.name ?: tile.sensor.friendlyName, state = type.state)
        is TileType.SimpleHumidity -> SimpleHumiditySensorTileCard(title = tile.name ?: tile.sensor.friendlyName, state = type.humidity)
        is TileType.SimpleNoTypeRaw -> SimpleNoTypeTileCard(title = tile.name ?: tile.sensor.friendlyName, state = type.state)
    }
}
