package com.tirexmurina.tilerboard.features.tileCreate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen.SensorsListScreen
import com.tirexmurina.tilerboard.features.tileCreate.presentation.TileCreateViewModel
import com.tirexmurina.tilerboard.features.tileCreate.ui.composables.SimpleIconSelectorLocked
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.cards.PlaceholderSimpleCard
import com.tirexmurina.tilerboard.features.util.cards.SensorCard
import com.tirexmurina.tilerboard.features.util.tileCards.TileCardsGrid
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

@Composable
fun TileCreateScreen(
    viewModel: TileCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onTileSaved: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorDialogTitle by rememberSaveable { mutableStateOf("") }
    var errorDialogText by rememberSaveable { mutableStateOf("") }
    var showSensorPicker by rememberSaveable { mutableStateOf(false) }
    var showKitPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                TileCreateViewModel.TileCreateEvent.ReturnBack -> onTileSaved()
                is TileCreateViewModel.TileCreateEvent.ShowErrorDialog -> {
                    showErrorDialog = true
                    errorDialogTitle = event.title
                    errorDialogText = event.text
                }
            }
        }
    }

    if (showErrorDialog) {
        SingleButtonDialog(
            title = errorDialogTitle,
            message = errorDialogText,
            onDismiss = { showErrorDialog = false }
        )
    }

    when (val uiState = state) {
        TileCreateViewModel.TileCreateState.Initial -> Unit
        TileCreateViewModel.TileCreateState.Loading -> LoadingScreen()
        is TileCreateViewModel.TileCreateState.Content -> {
            TileCreateContent(
                tilesList = uiState.tileList,
                kits = uiState.kits,
                selectedKitId = uiState.selectedKitId,
                sensor = uiState.sensor,
                canSpawnTile = uiState.canSpawnTile,
                canSaveTile = uiState.canSaveTile,
                onNavigateBack = onNavigateBack,
                onTryTileClick = viewModel::spawnTestTile,
                onSaveTileClick = viewModel::saveTile,
                onSelectSensorClick = { showSensorPicker = true },
                onSelectKitClick = { showKitPicker = true },
                onNameChanged = viewModel::updateName
            )

            if (showSensorPicker) {
                OverlayContainer(onDismiss = { showSensorPicker = false }) {
                    SensorsListScreen(
                        onNavigateBack = { showSensorPicker = false },
                        onSensorClick = {
                            viewModel.getSensorData(it)
                            showSensorPicker = false
                        }
                    )
                }
            }

            if (showKitPicker) {
                OverlayContainer(onDismiss = { showKitPicker = false }) {
                    KitPicker(
                        kits = uiState.kits,
                        onSelect = {
                            viewModel.selectKit(it)
                            showKitPicker = false
                        },
                        onClear = {
                            viewModel.clearKitSelection()
                            showKitPicker = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OverlayContainer(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss, indication = null, interactionSource = remember { MutableInteractionSource() }),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TileCreateContent(
    tilesList: List<Tile>,
    kits: List<Kit>,
    selectedKitId: Long?,
    sensor: Sensor?,
    canSpawnTile: Boolean,
    canSaveTile: Boolean,
    onNavigateBack: () -> Unit,
    onTryTileClick: () -> Unit,
    onSaveTileClick: () -> Unit,
    onSelectSensorClick: () -> Unit,
    onSelectKitClick: () -> Unit,
    onNameChanged: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Создание тайла") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
                }
            }
        )

        Row(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LeftPanelTileCreation(
                modifier = Modifier.weight(0.4f),
                sensor = sensor,
                selectedKitName = kits.firstOrNull { it.id == selectedKitId }?.name,
                canSaveTile = canSaveTile,
                canCreateTile = canSpawnTile,
                onSelectSensorClick = onSelectSensorClick,
                onSelectKitClick = onSelectKitClick,
                onTryTileClick = onTryTileClick,
                onSaveTileClick = onSaveTileClick,
                onNameChanged = onNameChanged
            )
            Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) { TileCardsGrid(tilesList) }
        }
    }
}

@Composable
private fun LeftPanelTileCreation(
    modifier: Modifier = Modifier,
    sensor: Sensor?,
    selectedKitName: String?,
    canSaveTile: Boolean,
    canCreateTile: Boolean,
    onSelectSensorClick: () -> Unit,
    onSelectKitClick: () -> Unit,
    onTryTileClick: () -> Unit,
    onSaveTileClick: () -> Unit,
    onNameChanged: (String) -> Unit
) {
    var tileName by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SimpleIconSelectorLocked(
                    hintText = "Нажмите чтобы выбрать иконку",
                    dialogTitle = "Выбор иконки",
                    dialogMessage = "Извините, пока доступна только эта иконка"
                )
                PlaceholderSimpleCard(
                    text = selectedKitName ?: "Нажмите чтобы выбрать набор",
                    onClick = onSelectKitClick
                )
            }
            TextField(
                value = tileName,
                onValueChange = {
                    tileName = it
                    onNameChanged(it)
                },
                label = { Text("Название тайла") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            Spacer(Modifier.height(16.dp))
            if (sensor == null) {
                PlaceholderSimpleCard(text = "Нажмите для выбора сенсора", onClick = onSelectSensorClick)
            } else {
                SensorCard(sensor = sensor, onClick = { onSelectSensorClick() })
            }
        }

        Button(onClick = onTryTileClick, modifier = Modifier.fillMaxWidth(), enabled = canCreateTile) {
            Text("Проверить тайл")
        }
        Button(onClick = onSaveTileClick, modifier = Modifier.fillMaxWidth(), enabled = canSaveTile) {
            Text("Сохранить тайл")
        }
    }
}

@Composable
private fun KitPicker(kits: List<Kit>, onSelect: (Long) -> Unit, onClear: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Выберите набор")
        Button(onClick = onClear) { Text("Без набора") }
        kits.forEach { kit ->
            PlaceholderSimpleCard(text = kit.name, onClick = { onSelect(kit.id) })
        }
    }
}
