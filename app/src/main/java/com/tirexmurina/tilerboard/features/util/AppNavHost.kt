package com.tirexmurina.tilerboard.features.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tirexmurina.tilerboard.features.home.ui.screen.homeScreen.HomeScreen
import com.tirexmurina.tilerboard.features.kitCreate.ui.KitCreateScreen
import com.tirexmurina.tilerboard.features.sensorsList.ui.screen.sensorsListScreen.SensorsListScreen
import com.tirexmurina.tilerboard.features.settings.ui.screen.settingsScreen.SettingsScreen
import com.tirexmurina.tilerboard.features.tileCreate.ui.TileCreateScreen
import com.tirexmurina.tilerboard.features.welcome.ui.screen.welcomeScreen.WelcomeScreen

const val ROUTE_WELCOME = "welcome"
const val ROUTE_HOME = "home"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_SENSORS_LIST = "sensors_list_screen"
const val ROUTE_TILE_CREATE = "tile_create"
const val ROUTE_KIT_CREATE = "kit_create"

@Composable
fun AppNavHost(startDestination: String = ROUTE_WELCOME) {
    val navController = rememberNavController()
    val activity = LocalContext.current as? Activity

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
                onNavigateSettings = { navController.navigate(ROUTE_SETTINGS) }
            )
        }

        composable (ROUTE_SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateSensorsList = { navController.navigate(ROUTE_SENSORS_LIST) },
                onNavigateAddTile = { navController.navigate(ROUTE_TILE_CREATE) },
                onNavigateAddKit = { navController.navigate(ROUTE_KIT_CREATE) }
            )
        }

        composable (ROUTE_SENSORS_LIST) {
            SensorsListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ROUTE_TILE_CREATE) {
            TileCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateHomeAndRestart = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(ROUTE_KIT_CREATE){
            KitCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onCloseApp = { activity?.finish() },
                onNavigateAddTile = { navController.navigate(ROUTE_TILE_CREATE) }
            )
        }
    }
}