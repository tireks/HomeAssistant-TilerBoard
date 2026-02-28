package com.tirexmurina.tilerboard.features.kitCreate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.CreateKitUseCase
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.UserHasKitsUseCase
import com.tirexmurina.tilerboard.shared.kit.util.KitCreationException
import com.tirexmurina.tilerboard.shared.kit.util.UserKitException
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTileByIdUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.LinkTileToKitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KitCreateViewModel @Inject constructor(
    private val createKitUseCase: CreateKitUseCase,
    private val userHasKitsUseCase: UserHasKitsUseCase,
    private val getTileByIdUseCase: GetTileByIdUseCase,
    private val linkTileToKitUseCase: LinkTileToKitUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<KitCreateState>(KitCreateState.Initial)
    val uiState: StateFlow<KitCreateState> = _uiState.asStateFlow()

    private val _needCloseAppState = MutableStateFlow(CloseAppState())
    val needCloseAppState: StateFlow<CloseAppState> = _needCloseAppState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<KitCreateEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var needCloseApp = true
    private var selectedTile: Tile? = null
    private var currentName = ""

    fun startScreen() {
        _uiState.value = KitCreateState.Loading
        viewModelScope.launch {
            try {
                needCloseApp = !userHasKitsUseCase()
                _needCloseAppState.value = CloseAppState(needCloseApp)
                emitContent(showWarnings = false)
            } catch (e: Exception) {
                errorHandler(UserKitException("Не удалось получить наборы пользователя. ${e.message}"))
            }
        }
    }

    fun onTileSelected(tileId: Long) {
        viewModelScope.launch {
            try {
                selectedTile = getTileByIdUseCase(tileId)
                emitContent(showWarnings = false)
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    fun checkName(name: String) {
        currentName = name
        emitContent(showWarnings = name.isEmpty())
    }

    fun saveKit() {
        viewModelScope.launch {
            if (currentName.isEmpty()) {
                emitContent(showWarnings = true)
                return@launch
            }
            val tile = selectedTile
            if (tile == null) {
                errorHandler(IllegalStateException("Для сохранения набора нужно выбрать первый тайл"))
                return@launch
            }
            try {
                val kitId = createKitUseCase(currentName, R.drawable.ic_fence)
                linkTileToKitUseCase(tile.id, kitId)
                _uiEvent.emit(KitCreateEvent.NavigateBack)
            } catch (e: Exception) {
                errorHandler(KitCreationException("Не удалось создать набор. ${e.message}"))
            }
        }
    }

    fun askForBackNavigation() {
        viewModelScope.launch {
            if (needCloseApp) _uiEvent.emit(KitCreateEvent.CloseApp) else _uiEvent.emit(KitCreateEvent.NavigateBack)
        }
    }

    fun openTilesSelector() {
        viewModelScope.launch { _uiEvent.emit(KitCreateEvent.NavigateToTileCreate) }
    }

    private fun emitContent(showWarnings: Boolean) {
        _uiState.value = KitCreateState.Content(
            showWarnings = showWarnings,
            blockButton = currentName.isEmpty() || selectedTile == null,
            selectedTile = selectedTile
        )
    }

    private fun errorHandler(exception: Exception) {
        viewModelScope.launch {
            _uiEvent.emit(KitCreateEvent.ShowError(title = "Произошла ошибка", text = exception.message.toString()))
        }
    }

    data class CloseAppState(val needCloseApp: Boolean = true)

    sealed interface KitCreateState {
        data object Initial : KitCreateState
        data object Loading : KitCreateState
        data class Content(
            val showWarnings: Boolean,
            val blockButton: Boolean,
            val selectedTile: Tile?
        ) : KitCreateState
    }

    sealed interface KitCreateEvent {
        data object NavigateToTileCreate : KitCreateEvent
        data class ShowError(val title: String, val text: String) : KitCreateEvent
        data object CloseApp : KitCreateEvent
        data object NavigateBack : KitCreateEvent
    }
}
