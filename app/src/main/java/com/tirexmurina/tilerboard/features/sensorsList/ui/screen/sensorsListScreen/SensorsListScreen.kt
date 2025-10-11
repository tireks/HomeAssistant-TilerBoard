@file:OptIn(ExperimentalMaterial3Api::class)

package com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.sensorsList.presentation.SensorsListViewModel
import com.tirexmurina.tilerboard.features.sensorsList.presentation.SensorsListViewModel.SensorsListState
import com.tirexmurina.tilerboard.features.sensorsList.ui.composables.SensorCard
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.ui.theme.TilerBoardTheme

@Composable
fun SensorsListScreen(
    viewModel: SensorsListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSensorClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is SensorsListState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SensorsListState.Error -> {
            //todo на будущее тут будем дергать нормальный компоузабл
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ошибка загрузки сенсоров")
            }
        }

        is SensorsListState.Content -> {
            SensorsListScreenContent(
                sensors = (uiState as SensorsListState.Content).sensors,
                searchQuery = (uiState as SensorsListState.Content).searchQuery,
                onNavigateBack = onNavigateBack,
                onSensorClick = { onSensorClick(it) },
                onSearchClick = { viewModel.searchSensors(it) }
            )
        }
    }
}

@Composable
fun SensorsListScreenContent(
    sensors: List<Sensor>,
    searchQuery: String,
    onNavigateBack: () -> Unit = {},
    onSensorClick: (String) -> Unit = {},
    onSearchClick: (String) -> Unit = {}
) {
    var queryText by rememberSaveable { mutableStateOf(searchQuery) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp)),
                            singleLine = true,
                            placeholder = { Text("Поиск сенсоров...") },
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            colors = TextFieldDefaults.colors(
                                // текст
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                // фон контейнера
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                // нижняя линия — убираем
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                // курсор
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        IconButton(onClick = { onSearchClick(queryText) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Поиск"
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sensors) { sensor ->
                SensorCard(
                    sensor = sensor,
                    onClick = { onSensorClick(it) }
                )
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
fun SensorsListScreenPreview() {
    val mockSensors = List(15) { index ->
        Sensor(
            entityId = "sensor.temp_${index + 1}",
            state = if (index % 3 == 0) "unavailable" else (18 + index).toString(),
            deviceClass = "temperature",
            friendlyName = "Температура ${index + 1}",
            unitOfMeasurement = "°C",
            lastChanged = "2025-09-24T13:20:51.298143+00:00",
            lastUpdated = "2025-09-24T13:20:51.298143+00:00"
        )
    }

    TilerBoardTheme {
        SensorsListScreenContent(
            sensors = mockSensors,
            searchQuery = "sas",
            onNavigateBack = {},
            onSensorClick = {}
        )
    }
}