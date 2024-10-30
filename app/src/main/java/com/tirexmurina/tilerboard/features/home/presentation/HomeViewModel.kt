package com.tirexmurina.tilerboard.features.home.presentation

import androidx.lifecycle.ViewModel
import com.tirexmurina.tilerboard.shared.kit.domain.usecase.GetKitsUseCase
import com.tirexmurina.tilerboard.shared.tile.domain.usecase.GetTilesByKitIdUseCase
import com.tirexmurina.tilerboard.shared.user.domain.usecase.AuthUserLocalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class HomeViewModel @Inject constructor (
    private val authUserLocalUseCase: AuthUserLocalUseCase,
    private val getKitsUseCase: GetKitsUseCase,
    private val getTilesByKitIdUseCase: GetTilesByKitIdUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<Ho>(InferenceResultState.Initial)
    val uiState: StateFlow<InferenceResultState> = _uiState.asStateFlow()
     
}