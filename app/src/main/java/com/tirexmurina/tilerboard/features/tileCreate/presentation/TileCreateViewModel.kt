package com.tirexmurina.tilerboard.features.tileCreate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetSensorDataByIdUseCase
import com.tirexmurina.tilerboard.shared.sensor.util.SensorDataFault
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.CreateTileUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTilesByKitIdUseCase
import com.tirexmurina.tilerboard.shared.tile.util.KitTileException
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
    private val createTileUseCase: CreateTileUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<TileCreateState>(TileCreateState.Initial)
    val uiState: StateFlow<TileCreateState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TileCreateEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var sensorLocalStore: Sensor? = null

    private var nameLocalStore: String? = null
    private var tileListLocalStore: MutableList<Tile> = mutableListOf()
    private var canSpawnTileLocalStore = false
    private var canSaveTileLocalStore = false
    private var kitIdLocalStore: Long? = null

    private var testTileAdded: Boolean = false

    init {
        _uiState.value = TileCreateState.Loading
        viewModelScope.launch {
            try {
                //todo работа с китом временная, пока не сделаю полнофункциональную работу с китами (пока их хотя бы не будет несколько)
                val kitList = getKitsUseCase()
                if (kitList.isNotEmpty()) {
                    kitIdLocalStore = kitList.last().id
                } else {
                    throw KitTileException("Набор не найден. Что-то пошло не так")
                }
                kitIdLocalStore?.let {
                    tileListLocalStore = getTilesByKitIdUseCase(it).toMutableList()
                }
                //todo тут по-хорошему нужно обратботать случай, когда список тайлов пуст, наверное

                showContent()
            } catch (e: Exception) {
                errorHandler(e)
            }

        }

    }

    fun getSensorData(entityId: String) {
        _uiState.value = TileCreateState.Loading
        canSaveTileLocalStore = false
        viewModelScope.launch {
            try {
                sensorLocalStore = getSensorDataByIdUseCase(entityId)
                validateTileSpawnAbility()
                showContent()
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    fun updateName(name: String) {
        nameLocalStore = name
        canSaveTileLocalStore = false
        showContent()
    }

    fun spawnTestTile() {
        _uiState.value = TileCreateState.Loading
        try {
            val sensor = sensorLocalStore
            if (!canSpawnTileLocalStore || sensor == null) throw IllegalStateException("Something went wrong. Tile cannot be shown")
            val testTile = Tile(
                id = 0,
                type = chooseTileType(),
                name = chooseName(),
                sensor = sensor
            )
            if (testTileAdded){
                tileListLocalStore.removeAt(tileListLocalStore.lastIndex)
            }
            tileListLocalStore.add(testTile)
            testTileAdded = true
            canSaveTileLocalStore = true
            showContent()
        } catch (e: Exception) {
            errorHandler(e)
        }
    }

    fun saveTile() {
        _uiState.value = TileCreateState.Loading
        viewModelScope.launch {
            try {
                val sensor = sensorLocalStore
                val kitId = kitIdLocalStore
                if (!canSaveTileLocalStore || sensor == null || kitId == null) throw IllegalStateException("Something went wrong. Tile cannot be saved")
                val name = nameLocalStore
                createTileUseCase(chooseTileType(), kitId, sensor.entityId, name)
                _uiEvent.emit(TileCreateEvent.ReturnToHomeAndRestart)
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    private fun showContent() {
        _uiState.value = TileCreateState.Content(
            sensor = sensorLocalStore,
            tileList = tileListLocalStore.toList(),
            canSpawnTile = canSpawnTileLocalStore,
            canSaveTile = canSaveTileLocalStore
        )
    }

    private fun chooseTileType() : TileType {
        val sensor = sensorLocalStore
        if (sensor == null) throw SensorDataFault("Sensor not found")
        val sensorType = sensor.deviceClass
        val sensorState = sensor.state
        if (sensorType == "temperature") return SimpleTemperature(sensorState.toDouble())
        if (sensorType == "light") return SimpleBinaryOnOff(chooseBinaryOnOffEnum(sensorState))
        if (sensorType == "humidity") return SimpleHumidity(sensorState.toDouble())
        return SimpleNoTypeRaw(sensorState)
    }

    private fun chooseName() : String? {
        return nameLocalStore
    }

    private fun validateTileSpawnAbility() {
        //todo тут будут все проверки, перед тем как разлочить создание тестового тайла
        if (sensorLocalStore != null)
            canSpawnTileLocalStore = true
        else
            canSpawnTileLocalStore = false
    }

    private fun errorHandler(exception: Exception) {
        //_uiState.value = TileCreateState.Error(exception.message.toString()) //todo пока временно одна ошибка на все
        viewModelScope.launch {
            _uiEvent.emit(TileCreateEvent.ShowErrorDialog(title = "Произошла ошибка", exception.message.toString()))
        }
        /*when(exception){
            is NullUserException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION NULL USER")
            }
        }*/
    }

    sealed interface TileCreateEvent {
        data object ReturnToHomeAndRestart : TileCreateEvent
        data class ShowErrorDialog(val title: String, val text: String) : TileCreateEvent
    }

    sealed interface TileCreateState {
        data object Initial : TileCreateState
        data object Loading : TileCreateState
        data class Content(
            val sensor: Sensor?,
            val tileList: List<Tile>,
            val canSpawnTile: Boolean,
            val canSaveTile: Boolean
        ) : TileCreateState
        /*data class Error(val message: String) : TileCreateState*/
    }
}

/*
suspend fun getKits(){
    try {
        val state = _uiState.value as? HomeState.Content ?: return
        val kitList = getKitsUseCase()
        _uiState.value = state.copy(
            staticKitList = StaticKitList.Content(kitList)
        )
        if (kitList.isNotEmpty()) {
            subscribeForTiles(kitList.first().id)
        }
    } catch ( exception : Exception){
        errorHandler(exception)
    }

}*/
