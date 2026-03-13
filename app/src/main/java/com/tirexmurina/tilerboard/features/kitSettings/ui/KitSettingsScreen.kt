package com.tirexmurina.tilerboard.features.kitSettings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tirexmurina.tilerboard.features.kitSettings.presentation.KitSettingsViewModel
import com.tirexmurina.tilerboard.features.tileCreate.ui.composables.SimpleIconSelectorLocked
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.tileCards.TileCardsGrid
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

@Composable
fun KitSettingsScreen(
    viewModel: KitSettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showKitPicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteKitDialog by rememberSaveable { mutableStateOf(false) }
    var tileActionDialogFor by remember { mutableStateOf<Tile?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                KitSettingsViewModel.KitSettingsEvent.NavigateBack -> onNavigateBack()
                is KitSettingsViewModel.KitSettingsEvent.ShowError -> errorMessage = event.message
            }
        }
    }

    errorMessage?.let {
        SingleButtonDialog(title = "Произошла ошибка", message = it, onDismiss = { errorMessage = null })
    }

    if (showDeleteKitDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteKitDialog = false },
            title = { Text("Удаление набора") },
            text = { Text("Вы уверены, что хотите удалить набор") },
            dismissButton = {
                TextButton(onClick = { showDeleteKitDialog = false }) { Text("Отмена") }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteKitDialog = false
                    viewModel.deleteCurrentKit()
                }) { Text("Да") }
            }
        )
    }

    tileActionDialogFor?.let { tile ->
        AlertDialog(
            onDismissRequest = { tileActionDialogFor = null },
            title = { Text("Действие с тайлом") },
            text = { Text("Удалить тайл или отвязать от текущего набора") },
            dismissButton = {
                TextButton(onClick = { tileActionDialogFor = null }) { Text("Отмена") }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        tileActionDialogFor = null
                        viewModel.deleteTile(tile.id)
                    }) { Text("Удалить") }
                    TextButton(onClick = {
                        tileActionDialogFor = null
                        viewModel.detachTile(tile.id)
                    }) { Text("Отвязать") }
                }
            }
        )
    }

    when (val uiState = state) {
        KitSettingsViewModel.KitSettingsState.Loading -> LoadingScreen()
        is KitSettingsViewModel.KitSettingsState.Content -> {
            KitSettingsContent(
                kits = uiState.kits,
                selectedKitName = uiState.selectedKitName,
                editedKitName = uiState.editedKitName,
                tiles = uiState.tiles,
                canSave = uiState.canSave,
                onNameChange = viewModel::updateName,
                onNavigateBack = onNavigateBack,
                onSelectKit = { showKitPicker = true },
                onDeleteKit = { showDeleteKitDialog = true },
                onSaveKit = viewModel::saveKit,
                onTileClick = { tileActionDialogFor = it }
            )

            if (showKitPicker) {
                OverlayContainer(onDismiss = { showKitPicker = false }) {
                    KitPicker(
                        kits = uiState.kits,
                        onSelect = {
                            viewModel.selectKit(it)
                            showKitPicker = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KitSettingsContent(
    kits: List<Kit>,
    selectedKitName: String,
    editedKitName: String,
    tiles: List<Tile>,
    canSave: Boolean,
    onNameChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSelectKit: () -> Unit,
    onDeleteKit: () -> Unit,
    onSaveKit: () -> Unit,
    onTileClick: (Tile) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Настройки набора") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
                }
            }
        )

        Row(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(0.4f)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SimpleIconSelectorLocked(
                            hintText = "Нажмите чтобы выбрать иконку",
                            dialogTitle = "Выбор иконки",
                            dialogMessage = "Извините, пока доступна только эта иконка"
                        )
                        Button(onClick = onSelectKit) { Text(selectedKitName) }
                    }
                    TextField(
                        value = editedKitName,
                        onValueChange = onNameChange,
                        label = { Text("Название набора") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Button(onClick = onDeleteKit, modifier = Modifier.fillMaxWidth()) { Text("Удалить набор") }
                Button(onClick = onSaveKit, modifier = Modifier.fillMaxWidth(), enabled = canSave) { Text("Сохранить набор") }
            }
            Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                TileCardsGrid(tiles = tiles, onTileClick = onTileClick)
            }
        }
    }
}

@Composable
private fun KitPicker(kits: List<Kit>, onSelect: (Long) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Выберите набор")
        kits.forEach { kit ->
            Button(onClick = { onSelect(kit.id) }, modifier = Modifier.fillMaxWidth()) {
                Text(kit.name)
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
