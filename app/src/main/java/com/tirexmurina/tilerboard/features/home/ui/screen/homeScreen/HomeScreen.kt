package com.tirexmurina.tilerboard.features.home.ui.screen.homeScreen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.home.presentation.homeScreen.DynamicTileList
import com.tirexmurina.tilerboard.features.home.presentation.homeScreen.HomeState
import com.tirexmurina.tilerboard.features.home.presentation.homeScreen.HomeViewModel
import com.tirexmurina.tilerboard.features.home.presentation.homeScreen.StaticKitList
import com.tirexmurina.tilerboard.features.util.TwoButtonDialog
import com.tirexmurina.tilerboard.features.util.tiles.SimpleBinaryTile
import com.tirexmurina.tilerboard.features.util.tiles.TemperatureSensorTile
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.ui.theme.TilerBoardTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateSettings: () -> Unit = {},
    //todo другие навигационные пути
){
    val viewState by viewModel.uiState.collectAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler {
        showExitDialog = true
    }

    when(viewState){
        is HomeState.Content -> {
            val content = viewState as HomeState.Content
            HomeScreenContent(
                kits = content.staticKitList,
                tiles = content.dynamicTilesList,
                onKitSelected = { kitId ->
                    viewModel.subscribeForTiles(kitId)
                },
                onNavigateSettings = { onNavigateSettings() }
            )
        }
        is HomeState.Error -> {

        }
        is HomeState.Initial -> {
            viewModel.startScreen()
        }
    }

    if (showExitDialog) {
        TwoButtonDialog(
            message = "Вы точно хотите выйти?",
            title = "Выход",
            onConfirm = {
                showExitDialog = false
                activity?.finish()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

/*@Composable
fun HomeScreenContent(

){
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.width(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(id = R.drawable.ic_home_image),
                contentDescription ="Small floating action button."

            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    *//*Text(text = "1", color = Color.Black)*//*
                    TemperatureSensorTile()
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "2", color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "3", color = Color.Black)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "4", color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "5", color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "6", color = Color.Black)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "7", color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "8", color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(4.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "9", color = Color.Black)
                }
            }
        }
    }
}*/

@Composable
fun HomeScreenContent(
    kits: StaticKitList,
    tiles: DynamicTileList,
    onKitSelected: (Long) -> Unit,
    onNavigateSettings: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LeftPanel(
            kits,
            onKitSelected = { onKitSelected(it) },
            onNavigateSettings = { onNavigateSettings() }
        )
        TilesGrid(tiles)
    }
}


@Composable
private fun LeftPanel(
    kits: StaticKitList,
    onKitSelected: (Long) -> Unit,
    onNavigateSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Колонка китов — занимает почти весь экран
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center // центрирование содержимого
        ) {
            KitsColumn(
                kits = kits,
                onKitSelected = onKitSelected
            )
        }

        // Кнопка настроек прижата к низу
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = "Настройки",
            modifier = Modifier
                .size(48.dp)
                .clickable { onNavigateSettings() }
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun KitsColumn(
    kits: StaticKitList,
    onKitSelected: (Long) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // содержимое по центру
    ) {
        when (kits) {
            is StaticKitList.Content -> {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(kits.listKits) { kit ->
                        Icon(
                            painter = painterResource(id = kit.iconResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { onKitSelected(kit.id) }
                                .padding(8.dp)
                        )
                    }
                }
            }

            is StaticKitList.Loading -> {
                CircularProgressIndicator()
            }

            is StaticKitList.Error -> {
                Text("Ошибка загрузки Kit")
            }
        }
    }
}

@Composable
fun TilesGrid(tiles: DynamicTileList) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (tiles) {
            is DynamicTileList.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tiles.listTiles) { tile ->
                        when (tile.type) {
                            is TileType.SimpleTemperature -> TemperatureSensorTile(tile.sensor.state.toDoubleOrNull())
                            /*is TileType.PressureSensor -> PressureSensorTile(tile)
                            else -> DefaultTile(tile)*/
                            is TileType.SimpleBinaryOnOff -> SimpleBinaryTile(tile.type.state)
                            else -> {}
                        }
                    }
                }
            }

            is DynamicTileList.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is DynamicTileList.Error -> {
                Text("Ошибка загрузки Tiles")
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
        HomeScreenContent(
            kits = StaticKitList.Content(
                listKits = listOf(
                    Kit(
                        id = 0,
                        "name",
                        R.drawable.ic_kit_icon_home
                    )
                )
            ),
            tiles = DynamicTileList.Content(
                listTiles = listOf(
                    Tile(
                        id = 0,
                        type = TileType.SimpleBinaryOnOff(null),
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
                )
            ),
            onKitSelected = {},
            onNavigateSettings = {}
        )
    }
}