package com.tirexmurina.tilerboard.features.home.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.features.home.presentation.HomeState
import com.tirexmurina.tilerboard.features.home.presentation.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
){
    val viewState by viewModel.uiState.collectAsState()





    when(viewState){
        is HomeState.Content -> {
            val content = viewState as HomeState.Content
            HomeScreenContent(
                kits = content.staticKitList,
                tiles = content.dynamicTilesList,
                onKitSelected = { kitId ->
                    viewModel.subscribeForTiles(kitId)
                }
            )
        }
        is HomeState.Error -> {

        }
        is HomeState.Initial -> {
            viewModel.startScreen()
        }
    }
}