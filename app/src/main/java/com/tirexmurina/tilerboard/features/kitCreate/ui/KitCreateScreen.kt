package com.tirexmurina.tilerboard.features.kitCreate.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.kitCreate.presentation.KitCreateViewModel
import com.tirexmurina.tilerboard.features.tileCreate.ui.composables.SimpleIconSelectorLocked
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.TwoButtonDialog
import com.tirexmurina.tilerboard.features.util.tileCards.SimpleNoTypeTileCard

@Composable
fun KitCreateScreen(
    viewModel: KitCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onCloseApp: () -> Unit = {},
    onNavigateAddTile: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val closeAppState by viewModel.needCloseAppState.collectAsState()
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorDialogTitle = ""
    var errorDialogText = ""
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.startScreen() }

    BackHandler {
        if (closeAppState.needCloseApp) showExitDialog = true else viewModel.askForBackNavigation()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                KitCreateViewModel.KitCreateEvent.NavigateToTileCreate -> onNavigateAddTile()
                is KitCreateViewModel.KitCreateEvent.ShowError -> {
                    showErrorDialog = true
                    errorDialogTitle = event.title
                    errorDialogText = event.text
                }
                KitCreateViewModel.KitCreateEvent.CloseApp -> onCloseApp()
                KitCreateViewModel.KitCreateEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    if (showErrorDialog) {
        SingleButtonDialog(title = errorDialogTitle, message = errorDialogText, onDismiss = { showErrorDialog = false })
    }

    if (showExitDialog) {
        TwoButtonDialog(
            message = "У вас нет ни одного набора. Выход с этого экрана приведет к закрытию приложения. Выйти?",
            title = "Выход",
            onConfirm = { showExitDialog = false; onCloseApp() },
            onDismiss = { showExitDialog = false }
        )
    }

    when (val uiState = state) {
        KitCreateViewModel.KitCreateState.Initial -> Unit
        KitCreateViewModel.KitCreateState.Loading -> LoadingScreen()
        is KitCreateViewModel.KitCreateState.Content -> KitCreateScreenContent(
            showWarning = uiState.showWarnings,
            blockButton = uiState.blockButton,
            selectedTileName = uiState.selectedTile?.name ?: uiState.selectedTile?.sensor?.friendlyName,
            onNavigateBack = {
                if (closeAppState.needCloseApp) showExitDialog = true else onNavigateBack()
            },
            onNameChanged = viewModel::checkName,
            onSelectTileClicked = viewModel::openTilesSelector,
            onSaveButtonClicked = viewModel::saveKit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitCreateScreenContent(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSelectTileClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit,
    showWarning: Boolean,
    selectedTileName: String?,
    blockButton: Boolean
) {
    var kitName by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Создание набора") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Назад")
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimpleIconSelectorLocked(
                    hintText = "Нажмите чтобы выбрать иконку",
                    dialogTitle = "Выбор иконки",
                    dialogMessage = "Извините, пока доступна только эта иконка"
                )

                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = kitName,
                        onValueChange = { kitName = it; onNameChanged(it) },
                        label = { Text("Название набора") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (kitName.isEmpty()) {
                        Text(
                            text = "Необходимо ввести название набора",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (!showWarning) Color.Gray else Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (selectedTileName == null) {
                        Button(onClick = onSelectTileClicked, modifier = Modifier.padding(top = 8.dp)) { Text("Выбрать тайл") }
                    } else {
                        Box(modifier = Modifier.padding(top = 8.dp)) {
                            SimpleNoTypeTileCard(state = "выбран", title = selectedTileName)
                        }
                        Button(onClick = onSelectTileClicked, modifier = Modifier.padding(top = 8.dp)) { Text("Изменить тайл") }
                    }
                }

                Button(onClick = onSaveButtonClicked, enabled = !blockButton) { Text("Сохранить") }
            }
        }
    }
}

/*
@Preview(
    name = "Nexus_9",
    device = Devices.NEXUS_9,
    showBackground = true
)
@Composable
fun ScreenPreview(){
    TilerBoardTheme {
        KitCreateScreenContent(
            onNavigateBack = {},
            showWarning = true,
            blockButton = true,
            onNameChanged = {},
            onSaveButtonClicked = {}
        )
    }
}*/
