package com.example.doublem.composants.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.doublem.KeyModifier
import com.example.doublem.navigation.AppNavigation

data class Shortcut(val shortcutKey: Int,
                    val modifiers: List<KeyModifier> = emptyList(),
                    val releaseModifiers: Boolean = true,) {

    // Commande HID pour les touches de controlles du clavier
    companion object {
        // Touche de base
        const val LEFT_CONTROL: KeyModifier = 0b1
        const val LEFT_ALT: KeyModifier = 0b100
        const val LEFT_GUI: KeyModifier = 0b1000
        const val RIGHT_ALT: KeyModifier = 0b100_0000
        const val RIGHT_GUI: KeyModifier = 0b1000_0000
        const val MODIFIER_LEFT_GUI: Int = 0x08 // Souvent, la touche Windows gauche est représentée par ce modificateur

    }

}

// Traduction d'un texte en commande HID pour un clavier AZERTY
fun charToKeyCode(char: Char): Int {
    val lowerChar = char.lowercaseChar() // Convertit le caractère en minuscule
    return when (lowerChar) {
        'a' -> KeyEvent.KEYCODE_Q
        'b' -> KeyEvent.KEYCODE_B
        'c' -> KeyEvent.KEYCODE_C
        'd' -> KeyEvent.KEYCODE_D
        'e' -> KeyEvent.KEYCODE_E
        'f' -> KeyEvent.KEYCODE_F
        'g' -> KeyEvent.KEYCODE_G
        'h' -> KeyEvent.KEYCODE_H
        'i' -> KeyEvent.KEYCODE_I
        'j' -> KeyEvent.KEYCODE_J
        'k' -> KeyEvent.KEYCODE_K
        'l' -> KeyEvent.KEYCODE_L
        'm' -> KeyEvent.KEYCODE_SEMICOLON // ';' sur QWERTY
        'n' -> KeyEvent.KEYCODE_N
        'o' -> KeyEvent.KEYCODE_O
        'p' -> KeyEvent.KEYCODE_P
        'q' -> KeyEvent.KEYCODE_A
        'r' -> KeyEvent.KEYCODE_R
        's' -> KeyEvent.KEYCODE_S
        't' -> KeyEvent.KEYCODE_T
        'u' -> KeyEvent.KEYCODE_U
        'v' -> KeyEvent.KEYCODE_V
        'w' -> KeyEvent.KEYCODE_Z
        'x' -> KeyEvent.KEYCODE_X
        'y' -> KeyEvent.KEYCODE_Y
        'z' -> KeyEvent.KEYCODE_W
        ' ' -> KeyEvent.KEYCODE_SPACE
        else -> KeyEvent.KEYCODE_UNKNOWN
    }
}

// Interface pour ce connecter à un appareil en bluetooth
@Composable
fun BluetoothUiConnection(bluetoothController: BluetoothController) {
    Log.i("toto","In BluetoothUiConnection")

    val context = LocalContext.current
    var isButtonInitVisible by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    }
        if (isButtonInitVisible) {
            Button(
                onClick = { bluetoothController.init(context.applicationContext)
                            isButtonInitVisible = false }
            ) {
                Text(text = "Initialize Bluetooth device with HID profile")
            }
        }
        else {

            val btOn = bluetoothController.status is BluetoothController.Status.Connected
            if(!btOn) {
                Button(
                    onClick = { context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)) }
                ) {
                    Text(text = "discover and Pair new devices")
                }
            }
            val waiting = bluetoothController.status is BluetoothController.Status.Waiting
            val disconnected = bluetoothController.status is BluetoothController.Status.Disconnected
            if (waiting or disconnected) {
                Button(
                    onClick = { bluetoothController.connectHost() }
                ) {
                    Text(text = "Bluetooth connect to host")
                }
            }
            Text(
                //modifier=Modifier.align(Alignment.CenterHorizontally),
                text = bluetoothController.status.display,

            )

            if (btOn) {
                Button(
                    onClick = { bluetoothController.release()}
                ) {
                    Text(text = "Bluetooth disconnect from host")
                }
            }


        }
}

@Composable
fun BluetoothDesk(bluetoothController: BluetoothController) {
    // Affichage une fois la connection bluetooth réalisé
    Column( modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {

        Spacer(Modifier.height(16.dp))

        AppNavigation(bluetoothController = bluetoothController)

        Spacer(Modifier.height(16.dp))

    }
}