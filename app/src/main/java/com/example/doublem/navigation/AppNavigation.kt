package com.example.doublem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doublem.composants.bluetooth.BluetoothController
import com.example.doublem.pages.CreateApp
import com.example.doublem.pages.HomeScreen

@Composable
fun AppNavigation(bluetoothController: BluetoothController) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.MAIN_SCREEN) {
        composable(AppDestinations.MAIN_SCREEN) {
            HomeScreen(navController = navController, bluetoothController)
        }
        composable(AppDestinations.CREATE_ELEMENT_SCREEN) {
            CreateApp(navController = navController)
        }
        // Définissez d'autres écrans et leurs actions ici.
    }
}