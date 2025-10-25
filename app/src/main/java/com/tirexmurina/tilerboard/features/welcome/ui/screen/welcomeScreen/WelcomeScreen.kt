package com.tirexmurina.tilerboard.features.welcome.ui.screen.welcomeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tirexmurina.tilerboard.features.util.LoadingScreen
import com.tirexmurina.tilerboard.features.util.SingleButtonDialog
import com.tirexmurina.tilerboard.features.util.TwoButtonDialog
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.ApiState.API_UNAVAILABLE
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.ApiState.CREDENTIALS_INVALID
import com.tirexmurina.tilerboard.features.welcome.presentation.welcomeScreen.WelcomeScreenViewModel.LoginState.LOGIN_INVALID
import com.tirexmurina.tilerboard.ui.theme.TilerBoardTheme

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var showHintDialog by rememberSaveable { mutableStateOf(false) }
    var currentLogin by rememberSaveable { mutableStateOf("") }
    val isLoading = state.isLoading

    LaunchedEffect(state.errorMessage) {
        showErrorDialog = state.errorMessage != null
    }
    LaunchedEffect(state.hintMessage) {
        showHintDialog = state.hintMessage != null
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                WelcomeScreenViewModel.WelcomeEvent.NavigateToHome -> onNavigateHome()
            }
        }
    }

    when {
        isLoading -> {
            LoadingScreen()
        }

        showErrorDialog -> {
            SingleButtonDialog(
                message = state.errorMessage ?: "",
                onDismiss = { showErrorDialog = false }
            )
        }

        showHintDialog -> {
            TwoButtonDialog(
                message = state.hintMessage ?: "",
                onDismiss = { showHintDialog = false },
                onConfirm = { viewModel.register(currentLogin) }
            )
        }

        else -> {
            WelcomeScreenContent(
                state = state,
                onLoginChanged = { currentLogin = it },
                onLoginClick = { viewModel.tryToLogin(it) },
                onCheckApiClick = { token, url -> viewModel.checkCredentials(token, url) }
            )
        }

    }
}

@Composable
fun WelcomeScreenContent(
    modifier: Modifier = Modifier,
    state: WelcomeScreenViewModel.WelcomeUiState,
    onLoginChanged: (String) -> Unit,
    onLoginClick: (String) -> Unit,
    onCheckApiClick: (String, String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val horizontalPadding = screenWidth / 5

    var url by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }

    val apiState = state.apiState
    val loginState = state.loginState
    val apiAvailable = state.apiAvailable
    val tokenSaved = state.token
    val urlSaved = state.url

    LaunchedEffect(tokenSaved, urlSaved) {
        tokenSaved?.let { token = it }
        urlSaved?.let { url = it }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = login,
                    onValueChange = {
                        login = it
                        onLoginChanged(it)
                    },
                    label = { Text("Логин") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = loginState?.hintContent ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (loginState == LOGIN_INVALID) Color.Red else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onLoginClick(login) },
                enabled = apiAvailable == true,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Text("Логин")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = apiState?.hintContent ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (apiState == API_UNAVAILABLE || apiState == CREDENTIALS_INVALID) Color.Red else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onCheckApiClick(token, url) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Проверить")
            }
        }
    }
}

@Preview(
    name = "Nexus_9",
    device = Devices.NEXUS_9,
    showBackground = true
)
@Composable
fun ScreenPreview(){
    TilerBoardTheme {
        WelcomeScreenContent(
            state = WelcomeScreenViewModel.WelcomeUiState(
                token = "sas",
                url = "sasURL",
                apiState = API_UNAVAILABLE
            ),
            onLoginChanged = {},
            onLoginClick = {},
            onCheckApiClick = { string: String, string1: String -> }
        )
    }
}