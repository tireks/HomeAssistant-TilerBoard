package com.tirexmurina.tilerboard.features.util

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tirexmurina.tilerboard.features.home.ui.screen.homeScreen.HomeScreen
import com.tirexmurina.tilerboard.features.settings.ui.screen.settingsScreen.SettingsScreen
import com.tirexmurina.tilerboard.features.welcome.ui.screen.welcomeScreen.WelcomeScreen

const val ROUTE_WELCOME = "welcome"
const val ROUTE_HOME = "home"

const val ROUTE_SETTINGS = "settings"

const val ROUTE_SENSORS_LIST = "sensors_list_screen"

@Composable
fun AppNavHost(startDestination: String = ROUTE_WELCOME) {
    val navController = rememberNavController()

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
                /*onNavigateSensorsList = { navController.navigate(ROUTE_SENSORS_LIST) }*/
            )
        }
    }
}