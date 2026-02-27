package com.tirexmurina.tilerboard.features.tileCreate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.kit.domain.entity.Kit
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.kit.util.NeedFirstKitException
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetSensorDataByIdUseCase
import com.tirexmurina.tilerboard.shared.sensor.util.SensorDataFault
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.CreateTileUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTilesByKitIdUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.LinkTileToKitUseCase
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
class TileCreateViewModel @Inject constructor(
    private val getKitsUseCase: GetKitsUseCase,
    private val getSensorDataByIdUseCase: GetSensorDataByIdUseCase,
    private val getTilesByKitIdUseCase: GetTilesByKitIdUseCase,
    private val createTileUseCase: CreateTileUseCase,
    private val linkTileToKitUseCase: LinkTileToKitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TileCreateState>(TileCreateState.Initial)
    val uiState: StateFlow<TileCreateState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TileCreateEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var sensorLocalStore: Sensor? = null
    private var nameLocalStore: String? = null
    private var selectedKitId: Long? = null
    private var kitsLocalStore: List<Kit> = emptyList()
    private var baseTilesForKit: List<Tile> = emptyList()
    private var previewTile: Tile? = null

    init {
        _uiState.value = TileCreateState.Loading
        viewModelScope.launch {
            try {
                kitsLocalStore = getKitsUseCase()
                showContent()
            } catch (e: Exception) {
                if (e is NeedFirstKitException){
                    kitsLocalStore = emptyList()
                    showContent()
                } else errorHandler(e)
            }
        }
    }

    fun getSensorData(entityId: String) {
        _uiState.value = TileCreateState.Loading
        viewModelScope.launch {
            try {
                sensorLocalStore = getSensorDataByIdUseCase(entityId)
                previewTile = null
                showContent()
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    fun updateName(name: String) {
        nameLocalStore = name
        previewTile = null
        showContent()
    }

    fun selectKit(kitId: Long) {
        _uiState.value = TileCreateState.Loading
        viewModelScope.launch {
            try {
                selectedKitId = kitId
                baseTilesForKit = getTilesByKitIdUseCase(kitId)
                previewTile = null
                showContent()
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    fun clearKitSelection() {
        selectedKitId = null
        baseTilesForKit = emptyList()
        previewTile = null
        showContent()
    }

    fun spawnTestTile() {
        try {
            val sensor = sensorLocalStore ?: throw IllegalStateException("Сначала выберите сенсор")
            previewTile = Tile(
                id = 0,
                type = chooseTileType(),
                name = chooseName(),
                sensor = sensor
            )
            showContent()
        } catch (e: Exception) {
            errorHandler(e)
        }
    }

    fun saveTile() {
        _uiState.value = TileCreateState.Loading
        viewModelScope.launch {
            try {
                val sensor = sensorLocalStore ?: throw IllegalStateException("Сенсор не выбран")
                val tileId = createTileUseCase(chooseTileType(), sensor.entityId, nameLocalStore)
                selectedKitId?.let { linkTileToKitUseCase(tileId, it) }
                _uiEvent.emit(TileCreateEvent.ReturnBack)
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    private fun showContent() {
        val currentPreview = previewTile
        val tiles = buildList {
            addAll(baseTilesForKit)
            currentPreview?.let { add(it) }
        }
        val canSpawn = sensorLocalStore != null
        val canSave = currentPreview != null
        _uiState.value = TileCreateState.Content(
            sensor = sensorLocalStore,
            tileList = tiles,
            kits = kitsLocalStore,
            selectedKitId = selectedKitId,
            canSpawnTile = canSpawn,
            canSaveTile = canSave
        )
    }

    private fun chooseTileType(): TileType {
        val sensor = sensorLocalStore ?: throw SensorDataFault("Sensor not found")
        val sensorType = sensor.deviceClass
        val sensorState = sensor.state
        if (sensorType == "temperature") return SimpleTemperature(sensorState.toDouble())
        if (sensorType == "light") return SimpleBinaryOnOff(chooseBinaryOnOffEnum(sensorState))
        if (sensorType == "humidity") return SimpleHumidity(sensorState.toDouble())
        return SimpleNoTypeRaw(sensorState)
    }

    private fun chooseName(): String? = nameLocalStore

    private fun errorHandler(exception: Exception) {
        viewModelScope.launch {
            _uiEvent.emit(TileCreateEvent.ShowErrorDialog(title = "Произошла ошибка", exception.message.toString()))
            showContent()
        }
    }

    sealed interface TileCreateEvent {
        data object ReturnBack : TileCreateEvent
        data class ShowErrorDialog(val title: String, val text: String) : TileCreateEvent
    }

    sealed interface TileCreateState {
        data object Initial : TileCreateState
        data object Loading : TileCreateState
        data class Content(
            val sensor: Sensor?,
            val tileList: List<Tile>,
            val kits: List<Kit>,
            val selectedKitId: Long?,
            val canSpawnTile: Boolean,
            val canSaveTile: Boolean
        ) : TileCreateState
    }
}
