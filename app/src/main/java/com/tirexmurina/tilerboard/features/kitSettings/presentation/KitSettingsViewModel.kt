package com.tirexmurina.tilerboard.features.kitSettings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.DeleteKitUseCase
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.UpdateKitUseCase
import com.tirexmurina.tilerboard.shared.kit.util.NeedFirstKitException
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.DeleteTileUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.DetachTileFromKitUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTilesByKitIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KitSettingsViewModel @Inject constructor(
    private val getKitsUseCase: GetKitsUseCase,
    private val getTilesByKitIdUseCase: GetTilesByKitIdUseCase,
    private val updateKitUseCase: UpdateKitUseCase,
    private val deleteKitUseCase: DeleteKitUseCase,
    private val detachTileFromKitUseCase: DetachTileFromKitUseCase,
    private val deleteTileUseCase: DeleteTileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<KitSettingsState>(KitSettingsState.Loading)
    val uiState: StateFlow<KitSettingsState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<KitSettingsEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var kits: List<Kit> = emptyList()
    private var selectedKitId: Long? = null
    private var editedName: String = ""

    init {
        loadInitial()
    }

    fun selectKit(kitId: Long) {
        selectedKitId = kitId
        editedName = kits.firstOrNull { it.id == kitId }?.name.orEmpty()
        emitContent()
    }

    fun updateName(name: String) {
        editedName = name
        emitContent()
    }

    fun saveKit() {
        viewModelScope.launch {
            val kitId = selectedKitId ?: return@launch
            val currentKit = kits.firstOrNull { it.id == kitId } ?: return@launch
            try {
                updateKitUseCase(currentKit.copy(name = editedName.trim()))
                _uiEvent.emit(KitSettingsEvent.NavigateBack)
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    fun deleteCurrentKit() {
        viewModelScope.launch {
            val kitId = selectedKitId ?: return@launch
            try {
                deleteKitUseCase(kitId)
                _uiEvent.emit(KitSettingsEvent.NavigateBack)
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    fun detachTile(tileId: Long) {
        viewModelScope.launch {
            val kitId = selectedKitId ?: return@launch
            try {
                detachTileFromKitUseCase(tileId, kitId)
                emitContent()
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    fun deleteTile(tileId: Long) {
        viewModelScope.launch {
            try {
                deleteTileUseCase(tileId)
                emitContent()
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            try {
                kits = getKitsUseCase()
                selectedKitId = kits.firstOrNull()?.id
                editedName = kits.firstOrNull()?.name.orEmpty()
                emitContent()
            } catch (e: Exception) {
                if (e is NeedFirstKitException) {
                    _uiEvent.emit(KitSettingsEvent.NavigateBack)
                } else showError(e)
            }
        }
    }

    private fun emitContent() {
        viewModelScope.launch {
            val kitId = selectedKitId ?: return@launch
            _uiState.value = KitSettingsState.Loading
            val tiles = getTilesByKitIdUseCase(kitId)
            _uiState.value = KitSettingsState.Content(
                kits = kits,
                selectedKitId = kitId,
                selectedKitName = kits.firstOrNull { it.id == kitId }?.name.orEmpty(),
                editedKitName = editedName,
                tiles = tiles,
                canSave = editedName.isNotBlank()
            )
        }
    }

    private fun showError(e: Exception) {
        viewModelScope.launch {
            _uiEvent.emit(KitSettingsEvent.ShowError(e.message ?: "Неизвестная ошибка"))
        }
    }

    sealed interface KitSettingsState {
        data object Loading : KitSettingsState
        data class Content(
            val kits: List<Kit>,
            val selectedKitId: Long,
            val selectedKitName: String,
            val editedKitName: String,
            val tiles: List<Tile>,
            val canSave: Boolean
        ) : KitSettingsState
    }

    sealed interface KitSettingsEvent {
        data class ShowError(val message: String) : KitSettingsEvent
        data object NavigateBack : KitSettingsEvent
    }
}
