package com.tirexmurina.tilerboard.features.settings.ui.screen.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.features.settings.presentation.SettingsViewModel
import com.tirexmurina.tilerboard.features.settings.ui.composables.SettingCard
import com.tirexmurina.tilerboard.ui.theme.TilerBoardTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateSensorsList: () -> Unit = {},
    onNavigateAddTile: () -> Unit = {},
    onNavigateKitSettings: () -> Unit = {},
    //сюда пойдут прочие навигационные вызовы
) {
    //когда нибудь здесь будет обработка стейта с вьюмодели,
    // но сейчас экран супер простой, поэтому вьюмодель вообще пустая и ничего не умеет
    SettingsScreenContent(
        onNavigateSensorsList = { onNavigateSensorsList() }
    )
}

@Composable
fun SettingsScreenContent(
    onNavigateSensorsList: () -> Unit = {},
    onNavigateAddTile: () -> Unit = {},
    onNavigateKitSettings: () -> Unit = {},
) {
    val items = listOf(
        "Список всех сенсоров" to onNavigateSensorsList,
        "Добавление тайла в набор" to onNavigateAddTile,
        "Настройки текущего набора" to onNavigateKitSettings
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { (title, onClick) ->
            SettingCard(title = title, onClick = onClick)
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
        SettingsScreenContent()
    }
}