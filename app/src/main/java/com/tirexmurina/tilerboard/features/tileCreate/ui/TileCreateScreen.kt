package com.tirexmurina.tilerboard.features.tileCreate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen.SensorsListScreen
import com.tirexmurina.tilerboard.features.tileCreate.presentation.TileCreateViewModel
import com.tirexmurina.tilerboard.features.tileCreate.presentation.TileCreateViewModel.TileCreateEvent.ReturnToHomeAndRestart
import com.tirexmurina.tilerboard.features.tileCreate.presentation.TileCreateViewModel.TileCreateEvent.ShowErrorDialog
import com.tirexmurina.tilerboard.features.tileCreate.ui.composables.SimpleIconSelectorLocked
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.cards.PlaceholderSimpleCard
import com.tirexmurina.tilerboard.features.util.cards.SensorCard
import com.tirexmurina.tilerboard.features.util.tileCards.TileCardsGrid
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.ui.theme.TilerBoardTheme

@Composable
fun TileCreateScreen(
    viewModel: TileCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateHomeAndRestart: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorDialogTitle = ""
    var errorDialogText = ""
    var showSensorPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ReturnToHomeAndRestart -> onNavigateHomeAndRestart()
                is ShowErrorDialog -> {
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

    when (state) {
        TileCreateViewModel.TileCreateState.Initial -> Unit
        TileCreateViewModel.TileCreateState.Loading -> LoadingScreen()
        is TileCreateViewModel.TileCreateState.Content -> {
            val contentState = state as TileCreateViewModel.TileCreateState.Content
            TileCreateScreenContent(
                tilesList = contentState.tileList,
                sensor = contentState.sensor,
                canSpawnTile = contentState.canSpawnTile,
                canSaveTile = contentState.canSaveTile,
                onNavigateBack = { onNavigateBack() },
                onTryTileClick = { viewModel.spawnTestTile() },
                onSaveTileClick = { viewModel.saveTile() },
                onSelectSensorClick = {
                    showSensorPicker = true
                },
                onNameChanged = { name -> viewModel.updateName(name) }

                //todo все остальное
            )
            if (showSensorPicker) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)) // затемнение
                        .clickable(
                            onClick = { showSensorPicker = false },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.8f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        SensorsListScreen(
                            onNavigateBack = { showSensorPicker = false },
                            onSensorClick = { selectedId ->
                                showSensorPicker = false
                                viewModel.getSensorData(selectedId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileCreateScreenContent(
    modifier: Modifier = Modifier,
    tilesList: List<Tile>,
    sensor: Sensor?,
    canSpawnTile: Boolean,
    canSaveTile: Boolean,
    onSelectSensorClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onSelectVisualContainerClick: () -> Unit = {},
    onSelectTileTypeClick: () -> Unit = {},
    onTryTileClick: () -> Unit = {},
    onSaveTileClick: () -> Unit = {},
    onNameChanged: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Создание тайла") },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Назад"
                    )
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ===== ЛЕВАЯ КОЛОНКА =====
            LeftPanelTileCreation(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                sensor = sensor,
                canSaveTile = canSaveTile,
                canCreateTile = canSpawnTile,
                onSelectSensorClick = { onSelectSensorClick() },
                onSelectVisualContainerClick = { onSelectVisualContainerClick() },
                onSelectTileTypeClick = { onSelectTileTypeClick() },
                onTryTileClick = { onTryTileClick() },
                onSaveTileClick = { onSaveTileClick() },
                onNameChanged = { onNameChanged(it) }
            )

            // ===== ПРАВАЯ КОЛОНКА =====
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(0.7f)
            ) {
                TileCardsGrid(tilesList)
            }
        }
    }
}

@Composable
private fun LeftPanelTileCreation(
    modifier: Modifier = Modifier,
    sensor: Sensor?,
    canSaveTile: Boolean,
    canCreateTile: Boolean,
    onSelectSensorClick: () -> Unit,
    onSelectVisualContainerClick: () -> Unit,
    onSelectTileTypeClick: () -> Unit,
    onTryTileClick: () -> Unit,
    onSaveTileClick: () -> Unit,
    onNameChanged: (String) -> Unit
) {
    var tileName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SimpleIconSelectorLocked(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    hintText = "Нажмите чтобы выбрать иконку",
                    dialogTitle = "Выбор иконки",
                    dialogMessage = "Извините, пока доступна только эта иконка"
                )
                SimpleIconSelectorLocked(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    hintText = "Нажмите чтобы выбрать набор",
                    dialogTitle = "Выбор набора",
                    dialogMessage = "Извините, пока доступен только один набор"
                )
            }
            // --- Поле выбора иконки ---


            TextField(
                value = tileName,
                onValueChange = { newValue ->
                    tileName = newValue
                    onNameChanged(newValue)
                },
                label = { Text(
                    text = "Название тайла"
                ) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            if (tileName.isEmpty()) {
                Text(
                    text = "По умолчанию - Friendly name сенсора",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            if (sensor == null) {
                PlaceholderSimpleCard(
                    text = "Нажмите для выбора сенсора",
                    onClick = { onSelectSensorClick() }
                )
            } else {
                SensorCard(
                    sensor = sensor,
                    onClick = { onSelectSensorClick() }
                )
            }
            Spacer(Modifier.height(16.dp))
            PlaceholderSimpleCard(
                text = "Пока выбор пока выбор типа тайла и правил поведения недоступен. Тип тайла будет выбран автоматически, в зависимости от типа сенсора"
            )
        }

        Column {
            Button(
                onClick = { onTryTileClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = canCreateTile
            ) {
                Text("Проверить тайл")
            }

            Button(
                onClick = { onSaveTileClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSaveTile
            ) {
                Text("Сохранить тайл")
            }
        }
    }
}

@Preview(
    name = "Nexus_9",
    device = Devices.NEXUS_9,
    showBackground = true
)
@Composable
fun ScreenPreview(){
    TilerBoardTheme {
        TileCreateScreenContent(
            tilesList = listOf(
                Tile(
                    id = 0,
                    type = TileType.SimpleTemperature(100.0),
                    name = null,
                    sensor = Sensor(
                        "1",
                        "2",
                        "3",
                        "4",
                        "5",
                        "6",
                        "7",
                    )
                ),
                Tile(
                    id = 0,
                    type = TileType.SimpleTemperature(100.0),
                    name = "null",
                    sensor = Sensor(
                        "1",
                        "2",
                        "3",
                        "4",
                        "5",
                        "6",
                        "7",
                    )
                )
            ),
            sensor = Sensor(
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
            ),
            canSpawnTile = true,
            canSaveTile = false
        )
    }
}

