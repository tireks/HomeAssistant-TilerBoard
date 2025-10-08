package com.tirexmurina.tilerboard.features.home.presentation.homeScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.kit.util.KitCreationException
import com.tirexmurina.tilerboard.shared.kit.util.NullUserException
import com.tirexmurina.tilerboard.shared.kit.util.UserKitException
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetSensorDataByIdUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.entity.Tile
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTilesByKitIdUseCase
import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum.OFF
import com.tirexmurina.tilerboard.shared.tile.util.BinaryOnOffEnum.ON
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleBinaryOnOff
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleHumidity
import com.tirexmurina.tilerboard.shared.tile.util.TileType.SimpleTemperature
import com.tirexmurina.tilerboard.shared.user.domain.usecase.AuthUserLocalUseCase
import com.tirexmurina.tilerboard.shared.user.util.DataBaseCorruptedException
import com.tirexmurina.tilerboard.shared.user.util.SharedPrefsCorruptedException
import com.tirexmurina.tilerboard.shared.user.util.TokenException
import com.tirexmurina.tilerboard.shared.user.util.UnknownException
import com.tirexmurina.tilerboard.shared.user.util.UserAccessLevel.Admin
import com.tirexmurina.tilerboard.shared.user.util.UserAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor (
    private val authUserLocalUseCase: AuthUserLocalUseCase,
    private val getKitsUseCase: GetKitsUseCase,
    private val getTilesByKitIdUseCase: GetTilesByKitIdUseCase,
    private val getSensorDataByIdUseCase: GetSensorDataByIdUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Initial)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private var currentKitId: Long? = null
    private var tilesJob: Job? = null
    private var localTileValue = ON

    fun startScreen() {
        _uiState.value = HomeState.Content(
            staticKitList = StaticKitList.Loading,
            dynamicTilesList = DynamicTileList.Loading
        )
        viewModelScope.launch {
            try {
                val authDeferred = async { testAuthUser() }
                authDeferred.await()
                getKits()
            } catch (exception: Exception) {
                errorHandler(exception)
            }
        }
    }

    private suspend fun testAuthUser(){
            try {
                authUserLocalUseCase("testAdmin", Admin)
            } catch ( exception : Exception) {
                errorHandler(exception)
            }
    }

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

    }



    fun subscribeForTiles(kitId : Long){ //todo нужен будет механизм ансабскрайба upd. пока есть вон кенсел джобы, только мне кажется он еще где-то должен быть
        tilesJob?.cancel()
        currentKitId = kitId
        tilesJob = viewModelScope.launch {
            val initialTilesList = getTilesByKitIdUseCase(kitId)
            generateTilesFlow(initialTilesList).collect { updatedTiles ->
                val state = _uiState.value as? HomeState.Content ?: return@collect
                _uiState.value = state.copy(
                    dynamicTilesList = DynamicTileList.Content(updatedTiles)
                )
            }
        }
    }

    private fun generateTilesFlow(initialTilesList: List<Tile>): Flow<List<Tile>> = flow {
        while (currentCoroutineContext().isActive) {
            val updatedTiles = coroutineScope {
                initialTilesList.map { tile ->
                    async {
                        try {
                            val updatedSensor = getSensorDataByIdUseCase(tile.sensor.entityId)
                            tile.copy(sensor = updatedSensor)
                        } catch (e: Exception) {
                            val tileType = tile.type
                            when(tileType){
                                //todo пока тут какая-то ерунда, но нормально пока не делал,
                                // нет смысла, текущую систему типов надо переделать
                                is SimpleBinaryOnOff -> tile.copy(type = SimpleBinaryOnOff(null))
                                is SimpleHumidity -> tile.copy(type = SimpleHumidity(null))
                                is SimpleTemperature -> tile.copy(type = SimpleTemperature(null))
                            }
                        }
                    }
                }.awaitAll()
            }
            emit(updatedTiles)
            delay(3000L) // Обновляем каждые 3 секунды
        }
    }

    private fun testTileProvider(tile: Tile): Tile {
        if (tile.type is SimpleBinaryOnOff) {
            localTileValue = if (localTileValue == ON) OFF else ON
            return tile.copy(type = SimpleBinaryOnOff(localTileValue))
        }
        return tile
    }

    private fun errorHandler(exception: Exception) {
        _uiState.value = HomeState.Error.TestError //todo пока временно одна ошибка на все
        when(exception){
            is NullUserException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION NULL USER")
            }
            is KitCreationException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION KIT CREATION")
            }
            is UserKitException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION USER KIT EXCEPTION")
            }
            is SharedPrefsCorruptedException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION SHARED PREFS SHIT")
            }
            is DataBaseCorruptedException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION DATABASE SHIT")
            }
            is UserAuthException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION USER AUTH")
            }
            is TokenException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION TOKEN SHIT")
            }
            is UnknownException -> {
                Log.d("EXCEPTIONSAS","FOCKING EXCOPTION DUNNO")
            }
        }
    }
     
}