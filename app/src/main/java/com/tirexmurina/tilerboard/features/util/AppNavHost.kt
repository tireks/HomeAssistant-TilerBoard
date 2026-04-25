package com.tirexmurina.tilerboard.features.util

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tirexmurina.tilerboard.features.automations.ui.screen.AutomationsScreen
import com.tirexmurina.tilerboard.features.home.ui.screen.homeScreen.HomeScreen
import com.tirexmurina.tilerboard.features.kitCreate.ui.KitCreateScreen
import com.tirexmurina.tilerboard.features.kitSettings.ui.KitSettingsScreen
import com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen.SensorsListScreen
import com.tirexmurina.tilerboard.features.settings.ui.screen.settingsScreen.SettingsScreen
import com.tirexmurina.tilerboard.features.tileCreate.ui.TileCreateScreen
import com.tirexmurina.tilerboard.features.tileSettings.ui.TileSettingsScreen
import com.tirexmurina.tilerboard.features.tilesList.ui.screen.tilesListScreen.TilesListScreen
import com.tirexmurina.tilerboard.features.welcome.ui.screen.welcomeScreen.WelcomeScreen
import com.tirexmurina.tilerboard.shared.automation.service.AutomationInAppAlertCenter

const val ROUTE_WELCOME = "welcome"
const val ROUTE_HOME = "home"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_SENSORS_LIST = "sensors_list_screen"
const val ROUTE_TILE_CREATE = "tile_create"
const val ROUTE_TILE_SETTINGS = "tile_settings"
const val ROUTE_KIT_CREATE = "kit_create"
const val ROUTE_KIT_SETTINGS = "kit_settings"
const val ROUTE_TILES_LIST = "tiles_list"
const val ROUTE_TILES_SETTINGS_LIST = "tiles_settings_list"
const val SELECTED_TILE_ID_KEY = "selectedTileId"
const val ROUTE_AUTOMATIONS = "automations"

@Composable
fun AppNavHost(startDestination: String = ROUTE_WELCOME) {
    val navController = rememberNavController()
    val activity = LocalContext.current as? Activity
    val currentAlert by AutomationInAppAlertCenter.currentAlert.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(ROUTE_WELCOME) {
            WelcomeScreen(
                onNavigateHome = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(ROUTE_HOME) {
            HomeScreen(
                onNavigateSettings = { navController.navigate(ROUTE_SETTINGS) },
                onNavigateCreateKit = { navController.navigate(ROUTE_KIT_CREATE) }
            )
        }

        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateSensorsList = { navController.navigate(ROUTE_SENSORS_LIST) },
                onNavigateAddTile = { navController.navigate(ROUTE_TILE_CREATE) },
                onNavigateAddKit = { navController.navigate(ROUTE_KIT_CREATE) },
                onNavigateKitSettings = { navController.navigate(ROUTE_KIT_SETTINGS) },
                onNavigateTilesSettings = { navController.navigate(ROUTE_TILES_SETTINGS_LIST) },
                onNavigateAutomations = { navController.navigate(ROUTE_AUTOMATIONS) }
            )
        }

        composable(ROUTE_AUTOMATIONS) {
            AutomationsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ROUTE_SENSORS_LIST) {
            SensorsListScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ROUTE_TILE_CREATE) {
            TileCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onTileSaved = { navController.popBackStack() }
            )
        }

        composable(ROUTE_TILES_LIST) {
            TilesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateTile = { navController.navigate(ROUTE_TILE_CREATE) },
                onTileSelected = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SELECTED_TILE_ID_KEY, it)
                    navController.popBackStack()
                }
            )
        }

        composable(ROUTE_TILES_SETTINGS_LIST) {
            TilesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onTileSelected = { tileId -> navController.navigate("$ROUTE_TILE_SETTINGS/$tileId") },
                showCreateButton = false
            )
        }

        composable(
            route = "$ROUTE_TILE_SETTINGS/{tileId}",
            arguments = listOf(navArgument("tileId") { type = NavType.LongType })
        ) {
            TileSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ROUTE_KIT_SETTINGS) { backStackEntry ->
            val selectedTileId = backStackEntry.savedStateHandle.get<Long>(SELECTED_TILE_ID_KEY)
            KitSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateAddTile = { navController.navigate(ROUTE_TILES_LIST) },
                selectedTileId = selectedTileId,
                onTileSelectionConsumed = {
                    backStackEntry.savedStateHandle.remove<Long>(SELECTED_TILE_ID_KEY)
                }
            )
        }

        composable(ROUTE_KIT_CREATE) { backStackEntry ->
            val selectedTileId = backStackEntry.savedStateHandle.get<Long>(SELECTED_TILE_ID_KEY)
            KitCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onKitSaved = { navController.popBackStack() },
                onCloseApp = { activity?.finish() },
                onNavigateAddTile = { navController.navigate(ROUTE_TILES_LIST) },
                selectedTileId = selectedTileId,
                onTileSelectionConsumed = {
                    backStackEntry.savedStateHandle.remove<Long>(SELECTED_TILE_ID_KEY)
                }
            )
        }
    }

    currentAlert?.let { alert ->
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .background(color = Color(0xFFB00020), shape = RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = alert.title,
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = alert.message,
                    color = Color.White,
                    fontSize = 27.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Button(
                    onClick = { AutomationInAppAlertCenter.dismissCurrentAlert() },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text("Закрыть", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}
