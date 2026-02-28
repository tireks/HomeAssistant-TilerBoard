@file:OptIn(ExperimentalMaterial3Api::class)

package com.tirexmurina.tilerboard.features.tilesList.ui.screen.tilesListScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.features.tilesList.presentation.TilesListViewModel
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile

@Composable
fun TilesListScreen(
    viewModel: TilesListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onCreateTile: () -> Unit = {},
    onTileSelected: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (val state = uiState) {
        TilesListViewModel.TilesListState.Loading -> LoadingScreen()
        is TilesListViewModel.TilesListState.Error -> Text(state.message)
        is TilesListViewModel.TilesListState.Content -> TilesListScreenContent(
            tiles = state.tiles,
            searchQuery = state.searchQuery,
            onNavigateBack = onNavigateBack,
            onCreateTile = onCreateTile,
            onTileSelected = onTileSelected,
            onSearch = viewModel::searchTiles
        )
    }
}

@Composable
private fun TilesListScreenContent(
    tiles: List<Tile>,
    searchQuery: String,
    onNavigateBack: () -> Unit,
    onCreateTile: () -> Unit,
    onTileSelected: (Long) -> Unit,
    onSearch: (String) -> Unit
) {
    var queryText by rememberSaveable { mutableStateOf(searchQuery) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp)),
                            singleLine = true,
                            placeholder = { Text("Поиск тайлов...") },
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        IconButton(onClick = { onSearch(queryText) }) {
                            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
                    }
                },
                actions = {
                    Button(onClick = onCreateTile) { Text("Создать тайл") }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tiles) { tile ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTileSelected(tile.id) }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(tile.name ?: tile.sensor.friendlyName)
                        Text(tile.sensor.entityId, style = MaterialTheme.typography.bodySmall)
                        Text(tile.sensor.state, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
