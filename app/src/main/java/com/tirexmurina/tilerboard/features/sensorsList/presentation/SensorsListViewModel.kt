package com.tirexmurina.tilerboard.features.sensorsList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.shared.sensor.domain.entity.Sensor
import com.tirexmurina.tilerboard.shared.sensor.domain.usecase.GetAllSensorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorsListViewModel @Inject constructor(
    private val getAllSensorsUseCase: GetAllSensorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SensorsListState>(SensorsListState.Loading)
    val uiState: StateFlow<SensorsListState> = _uiState.asStateFlow()

    private var localList : List<Sensor> = listOf()

    init {
        loadSensors()
    }

    private fun loadSensors() {
        viewModelScope.launch {
            try {
                localList = getAllSensorsUseCase()
                val sensors = localList
                _uiState.value = SensorsListState.Content(sensors)
            } catch (e: Exception) {
                _uiState.value = SensorsListState.Error(e.message ?: "Ошибка загрузки сенсоров")
            }
        }
    }

    fun searchSensors(queryRaw: String) {
        val query = queryRaw.trim()
        viewModelScope.launch {
            _uiState.value = SensorsListState.Loading
            try {
                val filteredSensors = if (query.isEmpty()) {
                    localList
                } else {
                    localList.filter { sensor ->
                        sensor.entityId.contains(query, ignoreCase = true) ||
                                sensor.state.contains(query, ignoreCase = true) ||
                                sensor.deviceClass.contains(query, ignoreCase = true) ||
                                sensor.friendlyName.contains(query, ignoreCase = true)
                    }
                }

                _uiState.value = SensorsListState.Content(
                    sensors = filteredSensors,
                    searchQuery = query
                )
            } catch (e: Exception) {
                _uiState.value = SensorsListState.Error("Ошибка поиска: ${e.message}")
            }
        }
    }

    sealed interface SensorsListState {
        data object Loading : SensorsListState
        data class Content(
            val sensors: List<Sensor>,
            val searchQuery: String = ""
        ) : SensorsListState
        data class Error(val message: String) : SensorsListState
    }
}