package com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.ApiState.API_AVAILABLE
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.ApiState.API_UNAVAILABLE
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.ApiState.CREDENTIALS_INVALID
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.LoginState.LOGIN_INVALID
import com.tirexmurina.tilerboard.shared.user.domain.usecase.CheckApiUseCase
import com.tirexmurina.tilerboard.shared.user.domain.usecase.CheckUserPresenceUseCase
import com.tirexmurina.tilerboard.shared.user.domain.usecase.GetSavedTokenUseCase
import com.tirexmurina.tilerboard.shared.user.domain.usecase.GetSavedUrlUseCase
import com.tirexmurina.tilerboard.shared.user.domain.usecase.LoginUserUseCase
import com.tirexmurina.tilerboard.shared.user.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val checkUserPresenceUseCase: CheckUserPresenceUseCase,
    private val getSavedTokenUseCase: GetSavedTokenUseCase,
    private val getSavedUrlUseCase: GetSavedUrlUseCase,
    private val checkApiUseCase: CheckApiUseCase
) : ViewModel() {

    private val URL_PATTERN = Regex("^http://\\d{1,3}(?:\\.\\d{1,3}){3}:\\d+/api/$")
    private val LOGIN_PATTERN = Regex("^[A-Za-z0-9]+$")

    private val _uiState = MutableStateFlow<WelcomeUiState>(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<WelcomeEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        checkSavedData()
    }

    fun checkSavedData() {
        try {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                hintMessage = null,
            )
            viewModelScope.launch {
                val savedToken = getSavedTokenUseCase()
                val savedUrl = getSavedUrlUseCase()
                checkCredentials(savedToken, savedUrl)
            }
        } catch (e : Exception) {
            errorHandler(e)
        }
    }

    fun checkCredentials(token: String?, url: String?){
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            hintMessage = null,
        )
        if (token != null && url != null && isValidUrl(url) && isValidToken(token)){
            checkApi(token, url)
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = null,
                hintMessage = null,
                token = token,
                url = url,
                apiAvailable = null,
                apiState = CREDENTIALS_INVALID
            )
        }
    }

    private fun checkApi(token: String, url: String) {
        viewModelScope.launch {
            try {
                val apiAvailable = checkApiUseCase(token, url)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    hintMessage = null,
                    token = token,
                    url = url,
                    apiAvailable = apiAvailable,
                    apiState = if (apiAvailable) {
                        API_AVAILABLE
                    } else {
                        API_UNAVAILABLE
                    }
                )
            } catch (e : Exception) {
                errorHandler(e)
            }
        }

    }

    fun tryToLogin(login: String?) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            hintMessage = null,
        )
        viewModelScope.launch {
            try {
                if (login.isNullOrEmpty() || !isValidLogin(login)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        hintMessage = null,
                        loginState = LOGIN_INVALID
                    )
                } else {
                    if (checkUserPresenceUseCase(login)) {
                        loginUserUseCase(login)
                        _uiEvent.emit(WelcomeEvent.NavigateToHome)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            hintMessage = "Пользователь с таким логином не найден на этом устройстве. Хотите создать?"
                        )
                    }
                }
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    fun register(login: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            hintMessage = null,
        )
        viewModelScope.launch {
            try {
                registerUserUseCase(login)
                _uiEvent.emit(WelcomeEvent.NavigateToHome)
            } catch (e: Exception) {
                errorHandler(e)
            }
        }
    }

    private fun isValidUrl(url: String) : Boolean {
        return URL_PATTERN.matches(url.trim())

    }

    private fun isValidToken(token: String): Boolean {
        return token.isNotBlank() && !token.contains("Bearer", ignoreCase = true)
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotBlank() && LOGIN_PATTERN.matches(login)
    }

    private fun errorHandler(exception: Exception) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = "some error code " + exception.message.toString()
        )
        //todo потом надо будет сделать что-то с обработкой ошибок нормальное
    }

    sealed interface WelcomeEvent {
        data object NavigateToHome : WelcomeEvent
    }

    data class WelcomeUiState(
        val isLoading: Boolean = false,
        val token: String? = null,
        val url: String? = null,
        val apiAvailable: Boolean? = null,
        val apiState: ApiState? = null,
        val loginState: LoginState? = null,
        val errorMessage: String? = null,
        val hintMessage: String? = null
    )


    /*sealed interface WelcomeState {
        data object Initial : WelcomeState
        data object Loading : WelcomeState
        data class Content(
            val token: String? = null,
            val url: String? = null,
            val apiAvailable: Boolean? = null,
            val apiState: ApiState = API_UNAVAILABLE,
            val loginState: LoginState = LOGIN_INVALID
            ) : WelcomeState
        data object Error : WelcomeState
    }*/

    enum class ApiState(val hintContent: String) {
        CREDENTIALS_INVALID(
            "Проверьте корректность написания URL и token. " +
                    "URL записывается в формате http://0.0.0.0:8123/api/." +
                    "token записывается без слова Bearer"
        ),
        API_UNAVAILABLE(
            "Проверьте правильность URL и token. По введенным данным API недоступно"
        ),
        API_AVAILABLE(
            "Получен ответ от API. Можно авторизироваться"
        )
    }

    enum class LoginState(val hintContent: String) {
        LOGIN_INVALID(
            "Проверьте правильность логина. Разрешены только латинские буквы (A–Z, a–z) и цифры (0–9)"
        )
    }
}