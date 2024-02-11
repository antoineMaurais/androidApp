package com.example.doublem.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doublem.composants.app.AppListWithDAO
import com.example.doublem.composants.bluetooth.BluetoothController
import com.example.doublem.composants.bluetooth.BluetoothInteractionHandler
import com.example.doublem.data.AppViewModelProvider
import com.example.doublem.navigation.AppDestinations

// Page principale une fois la connexion bluetooth établit
@Composable
fun HomeScreen(navController: NavController, bluetoothController: BluetoothController) {
    val context = LocalContext.current

    val status = bluetoothController.status
    if (status is BluetoothController.Status.Connected) {
        // Initialize BluetoothInteractionHandler with BluetoothController and Context
        val bluetoothInteractionHandler = remember {
            BluetoothInteractionHandler(bluetoothController, context)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))


            Text(text = "DoubleM app's!")

            AppListWithDAO(bluetoothInteractionHandler = bluetoothInteractionHandler, viewModel = viewModel(factory = AppViewModelProvider.Factory))

            Button(onClick = { navController.navigate(AppDestinations.CREATE_ELEMENT_SCREEN) }) {
                Text("Add an app")
            }


            Spacer(Modifier.height(16.dp))
        }
    } else {
        // Handle the case where Bluetooth is not connected
        // This could be showing a message or providing an option to connect to Bluetooth
        Text(text = "Please connect to a Bluetooth device.")
    }
}