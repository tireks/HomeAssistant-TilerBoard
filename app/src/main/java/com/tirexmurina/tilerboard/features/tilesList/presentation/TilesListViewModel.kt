package com.tirexmurina.tilerboard.features.tilesList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetAllTilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TilesListViewModel @Inject constructor(
    private val getAllTilesUseCase: GetAllTilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TilesListState>(TilesListState.Loading)
    val uiState: StateFlow<TilesListState> = _uiState.asStateFlow()

    private var localTiles: List<Tile> = emptyList()

    init {
        loadTiles()
    }

    private fun loadTiles() {
        viewModelScope.launch {
            _uiState.value = TilesListState.Loading
            try {
                localTiles = getAllTilesUseCase()
                _uiState.value = TilesListState.Content(tiles = localTiles)
            } catch (e: Exception) {
                _uiState.value = TilesListState.Error(e.message ?: "Ошибка загрузки тайлов")
            }
        }
    }

    fun searchTiles(queryRaw: String) {
        val query = queryRaw.trim()
        val filtered = if (query.isEmpty()) {
            localTiles
        } else {
            localTiles.filter {
                it.sensor.friendlyName.contains(query, ignoreCase = true) ||
                    (it.name?.contains(query, ignoreCase = true) == true) ||
                    it.sensor.entityId.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = TilesListState.Content(tiles = filtered, searchQuery = query)
    }

    fun refresh() = loadTiles()

    sealed interface TilesListState {
        data object Loading : TilesListState
        data class Content(val tiles: List<Tile>, val searchQuery: String = "") : TilesListState
        data class Error(val message: String) : TilesListState
    }
}
