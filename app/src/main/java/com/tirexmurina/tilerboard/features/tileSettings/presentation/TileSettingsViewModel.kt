package com.tirexmurina.tilerboard.features.tileSettings.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetSensorDataByIdUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.DeleteTileUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTileByIdUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.UpdateTileUseCase
import com.tirexmurina.tilerboard.shared.tile.util.TileType
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleHumidity
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleNoTypeRaw
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleTemperature
import com.tirexmurina.tilerboard.shared.tile.util.chooseBinaryOnOffEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TileSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTileByIdUseCase: GetTileByIdUseCase,
    private val getSensorDataByIdUseCase: GetSensorDataByIdUseCase,
    private val updateTileUseCase: UpdateTileUseCase,
    private val deleteTileUseCase: DeleteTileUseCase
) : ViewModel() {

    private val tileId: Long = checkNotNull(savedStateHandle["tileId"])

    private val _uiState = MutableStateFlow<TileSettingsState>(TileSettingsState.Loading)
    val uiState: StateFlow<TileSettingsState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TileSettingsEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private lateinit var sourceTile: Tile
    private var sensor: Sensor? = null
    private var name: String = ""
    private var previewTile: Tile? = null

    init {
        loadTile()
    }

    fun updateName(newName: String) {
        name = newName
        previewTile = null
        emitContent()
    }

    fun selectSensor(entityId: String) {
        viewModelScope.launch {
            try {
                sensor = getSensorDataByIdUseCase(entityId)
                previewTile = null
                emitContent()
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    fun spawnPreview() {
        try {
            val newTile = Tile(
                id = sourceTile.id,
                type = chooseTileType(),
                name = name.ifBlank { null },
                sensor = sensor ?: sourceTile.sensor
            )
            previewTile = newTile
            emitContent()
        } catch (e: Exception) {
            showError(e)
        }
    }

    fun saveTile() {
        viewModelScope.launch {
            try {
                val tileToSave = previewTile ?: throw IllegalStateException("Сначала нажмите Проверить тайл")
                updateTileUseCase(tileToSave)
                _uiEvent.emit(TileSettingsEvent.NavigateBack)
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    fun deleteTile() {
        viewModelScope.launch {
            try {
                deleteTileUseCase(tileId)
                _uiEvent.emit(TileSettingsEvent.NavigateBack)
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    private fun loadTile() {
        viewModelScope.launch {
            try {
                sourceTile = getTileByIdUseCase(tileId)
                sensor = sourceTile.sensor
                name = sourceTile.name.orEmpty()
                emitContent()
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    private fun emitContent() {
        val currentSensor = sensor
        val shownTile = previewTile ?: if (::sourceTile.isInitialized) sourceTile.copy(
            sensor = currentSensor ?: sourceTile.sensor,
            name = name.ifBlank { null }
        ) else null

        if (shownTile != null) {
            _uiState.value = TileSettingsState.Content(
                tile = shownTile,
                sensor = currentSensor,
                tileName = name,
                canTest = currentSensor != null,
                canSave = previewTile != null
            )
        }
    }

    private fun chooseTileType(): TileType {
        val currentSensor = sensor ?: throw IllegalStateException("Сначала выберите сенсор")
        val sensorType = currentSensor.deviceClass
        val sensorState = currentSensor.state
        if (sensorType == "temperature") return SimpleTemperature(sensorState.toDoubleOrNull())
        if (sensorType == "light") return SimpleBinaryOnOff(chooseBinaryOnOffEnum(sensorState))
        if (sensorType == "humidity") return SimpleHumidity(sensorState.toDoubleOrNull())
        return SimpleNoTypeRaw(sensorState)
    }

    private fun showError(e: Exception) {
        viewModelScope.launch { _uiEvent.emit(TileSettingsEvent.ShowError(e.message ?: "Ошибка")) }
    }

    sealed interface TileSettingsState {
        data object Loading : TileSettingsState
        data class Content(
            val tile: Tile,
            val sensor: Sensor?,
            val tileName: String,
            val canTest: Boolean,
            val canSave: Boolean
        ) : TileSettingsState
    }

    sealed interface TileSettingsEvent {
        data class ShowError(val message: String) : TileSettingsEvent
        data object NavigateBack : TileSettingsEvent
    }
}
