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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.kitCreate.presentation.KitCreateViewModel
import com.tirexmurina.tilerboard.features.kitCreate.presentation.KitCreateViewModel.KitCreateEvent
import com.tirexmurina.tilerboard.features.kitCreate.presentation.KitCreateViewModel.KitCreateState
import com.tirexmurina.tilerboard.features.tileCreate.ui.composables.SimpleIconSelectorLocked
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.TwoButtonDialog
import com.tirexmurina.tilerboard.ui.theme.TilerBoardTheme

@Composable
fun KitCreateScreen(
    viewModel: KitCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onCloseApp:() -> Unit = {}, //todo на уровне над контентом нужно будет решать, какой из этих двух методов передавать вниз, в зависимости от ситуации
    onNavigateAddTile: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val closeAppState by viewModel.needCloseAppState.collectAsState()
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorDialogTitle = ""
    var errorDialogText = ""
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.startScreen()
    }

    BackHandler {
        if (closeAppState.needCloseApp){
            showExitDialog = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                KitCreateEvent.NavigateToTileCreate -> onNavigateAddTile()
                is KitCreateEvent.ShowError -> {
                    showErrorDialog = true
                    errorDialogTitle = event.title
                    errorDialogText = event.text
                }
                KitCreateEvent.CloseApp -> onCloseApp()
                KitCreateEvent.NavigateBack -> onNavigateBack()
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

    if (showExitDialog) {
        TwoButtonDialog(
            message = "У вас нет ни одного набора. Выход с этого экрана приведет к закрытию приложения. Выйти?",
            title = "Выход",
            onConfirm = {
                showExitDialog = false
                onCloseApp()
            },
            onDismiss = { showExitDialog = false }
        )
    }

    when (state) {
        KitCreateState.Initial -> Unit

        KitCreateState.Loading -> LoadingScreen()

        is KitCreateState.Content -> {
            val state = state as KitCreateState.Content
            KitCreateScreenContent(
                showWarning = state.showWarnings,
                blockButton = state.blockButton,
                onNavigateBack = {
                    if (closeAppState.needCloseApp){
                        showExitDialog = true
                    } else {
                        onNavigateBack()
                    }
                },
                onNameChanged = { viewModel.checkName(it) },
                onSaveButtonClicked = { viewModel.saveKit(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitCreateScreenContent(
    modifier: Modifier = Modifier,
    picRes: Int? = null,
    onNavigateBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSaveButtonClicked: (String) -> Unit,
    showWarning: Boolean,
    blockButton: Boolean
) {
    var kitName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Создание набора") },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Назад"
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SimpleIconSelectorLocked(
                    modifier = Modifier
                        .padding(end = 8.dp),
                    hintText = "Нажмите чтобы выбрать иконку",
                    dialogTitle = "Выбор иконки",
                    dialogMessage = "Извините, пока доступна только эта иконка"
                )

                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(horizontal = 8.dp)
                ) {
                    TextField(
                        value = kitName,
                        onValueChange = { newValue ->
                            kitName = newValue
                            onNameChanged(newValue)
                        },
                        label = { Text("Название набора") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (kitName.isEmpty()) {
                        Text(
                            text = "Необходимо ввести название тайла",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (!showWarning) Color.Gray else Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Button(
                    onClick = { onSaveButtonClicked(kitName) },
                    modifier = Modifier
                        .weight(0.6f)
                        .align(Alignment.CenterVertically),
                ) {
                    Text("Выбрать тайл")
                }
                Button(
                    onClick = { onSaveButtonClicked(kitName) },
                    modifier = Modifier
                        .weight(0.6f)
                        .align(Alignment.CenterVertically),
                    enabled = !blockButton
                ) {
                    Text("Сохранить")
                }
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
        KitCreateScreenContent(
            onNavigateBack = {},
            showWarning = true,
            blockButton = true,
            onNameChanged = {},
            onSaveButtonClicked = {}
        )
    }
}