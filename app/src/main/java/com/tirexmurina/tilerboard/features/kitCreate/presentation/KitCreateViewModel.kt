package com.tirexmurina.tilerboard.features.kitCreate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.R
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.CreateKitUseCase
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.UserHasKitsUseCase
import com.tirexmurina.tilerboard.shared.kit.util.KitCreationException
import com.tirexmurina.tilerboard.shared.kit.util.UserKitException
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
    private val userHasKitsUseCase: UserHasKitsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<KitCreateState>(KitCreateState.Initial)
    val uiState: StateFlow<KitCreateState> = _uiState.asStateFlow()

    private val _needCloseAppState = MutableStateFlow<CloseAppState>(CloseAppState())
    val needCloseAppState: StateFlow<CloseAppState> = _needCloseAppState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<KitCreateEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var needCloseApp = true

    fun startScreen() {
        _uiState.value = KitCreateState.Loading
        viewModelScope.launch {
            try {
                needCloseApp = userHasKitsUseCase()
                _needCloseAppState.value = CloseAppState(needCloseApp)
                _uiState.value = KitCreateState.Content(
                    showWarnings = false,
                    blockButton = true
                )
            } catch (e: Exception) {
                errorHandler(UserKitException("Не удалось получить наборы пользователя. " + e.message.toString()))
            }
        }
    }

    fun checkName(name: String){
        if (name.isEmpty()) {
            _uiState.value = KitCreateState.Content(
                showWarnings = true,
                blockButton = true
            )
        }
        else {
            _uiState.value = KitCreateState.Content(
                showWarnings = false,
                blockButton = false
            )
        }
    }

    fun saveKit(name: String){
        viewModelScope.launch {
            if (name.isNotEmpty()){
                try {
                    createKitUseCase(name, R.drawable.ic_fence)
                    _uiEvent.emit(KitCreateEvent.NavigateToTileCreate)
                } catch (e: Exception) {
                    errorHandler(KitCreationException("Не удалось создать набор. " + e.message.toString()))
                }

            } else {
                errorHandler(IllegalStateException("Имя не должно быть пустым"))
            }
        }

    }

    fun askForBackNavigation() {
        viewModelScope.launch {
            if (needCloseApp) {
                _uiEvent.emit(KitCreateEvent.CloseApp)
            } else {
                _uiEvent.emit(KitCreateEvent.NavigateBack)
            }
        }

    }

    private fun errorHandler(exception: Exception) {
         //todo пока временно одна ошибка на все
        viewModelScope.launch {
            _uiEvent.emit(KitCreateEvent.ShowError(title = "Произошла ошибка", exception.message.toString()))
        }
    }

    data class CloseAppState(
        val needCloseApp: Boolean = true
    )

    sealed interface KitCreateState {
        data object Initial: KitCreateState
        data object Loading: KitCreateState
        data class Content(
            val showWarnings: Boolean,
            val blockButton: Boolean
        ): KitCreateState
    }

    sealed interface KitCreateEvent {
        data object NavigateToTileCreate: KitCreateEvent
        data class ShowError(val title: String, val text: String): KitCreateEvent
        data object CloseApp: KitCreateEvent
        data object NavigateBack: KitCreateEvent
    }
}