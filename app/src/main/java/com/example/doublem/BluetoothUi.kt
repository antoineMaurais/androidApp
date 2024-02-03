package com.example.doublem

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bluetoothsample.KeyboardSender

data class Shortcut( val shortcutKey: Int,
                     val modifiers: List<KeyModifier> = emptyList(),
                     val releaseModifiers: Boolean = true,) {

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

fun charToKeyCode(char: Char): Int {
    return when (char) {
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

@Composable
fun BluetoothUiConnection(bluetoothController: BluetoothController) {
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

            Icon(
                if (btOn) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                "bluetooth",
                modifier = Modifier.size(100.dp),
                tint = if (btOn) Color.Blue else Color.Black,
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

        val connected = bluetoothController.status as? BluetoothController.Status.Connected ?: return

        val context = LocalContext.current
        val keyboardSender = KeyboardSender(connected.btHidDevice, connected.hostDevice)


        fun press(shortcut: Shortcut, releaseModifiers: Boolean = true) {
            @SuppressLint("MissingPermission")
            val result = keyboardSender.sendKeyboard(shortcut.shortcutKey, shortcut.modifiers, releaseModifiers)
            if (!result) Toast.makeText(context,"can't find keymap for $shortcut",Toast.LENGTH_LONG).show()
        }

        Column( modifier = Modifier.fillMaxWidth().padding(20.dp)) {

            Spacer(modifier = Modifier.size(20.dp))
            Text("Slide Desk")
            Spacer(modifier = Modifier.size(10.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = { press(Shortcut(KeyEvent.KEYCODE_DPAD_LEFT)) }) {
                    Text("<-")
                }
                Spacer(modifier = Modifier.size(20.dp))
                Button(onClick = { press(Shortcut(KeyEvent.KEYCODE_DPAD_RIGHT)) }) {
                    Text("->")
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

//            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
//                Button(onClick = {press(Shortcut(KeyEvent.KEYCODE_DPAD_LEFT,listOf(Shortcut.LEFT_ALT, Shortcut.LEFT_GUI)) )}) {
//                    Text("<- tab")
//                }
//                Spacer(modifier = Modifier.size(20.dp))
//                Button(onClick = {press(Shortcut( KeyEvent.KEYCODE_DPAD_RIGHT,listOf(Shortcut.RIGHT_ALT, Shortcut.RIGHT_GUI)))}) {
//                    Text("tab ->")
//                }
//            }
//
//            Spacer(modifier = Modifier.size(10.dp))

//            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
//                Button(onClick = {press(Shortcut(KeyEvent.KEYCODE_F,listOf(Shortcut.LEFT_CONTROL, Shortcut.LEFT_GUI)))}) {
//                    Text("full screen")
//                }
//            }


            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = {
                    press(Shortcut(KeyEvent.KEYCODE_TAB, listOf(Shortcut.LEFT_ALT)))
                }) {
                    Text("ALT TAB")
                }
            }

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = {
                    press(Shortcut(KeyEvent.KEYCODE_Q)) // Supposant que KEYCODE_A envoie "a"
                    press(Shortcut(KeyEvent.KEYCODE_N)) // Supposant que KEYCODE_B envoie "b"
                    press(Shortcut(KeyEvent.KEYCODE_T))
                    press(Shortcut(KeyEvent.KEYCODE_O))
                    press(Shortcut(KeyEvent.KEYCODE_I))
                    press(Shortcut(KeyEvent.KEYCODE_N))
                    press(Shortcut(KeyEvent.KEYCODE_E))

                    press(Shortcut(KeyEvent.KEYCODE_SPACE))

                    press(Shortcut(KeyEvent.KEYCODE_L))
                    press(Shortcut(KeyEvent.KEYCODE_Q))

                    press(Shortcut(KeyEvent.KEYCODE_SPACE))

                    press(Shortcut(KeyEvent.KEYCODE_P))
                    press(Shortcut(KeyEvent.KEYCODE_E))
                    press(Shortcut(KeyEvent.KEYCODE_T))
                    press(Shortcut(KeyEvent.KEYCODE_I))
                    press(Shortcut(KeyEvent.KEYCODE_T))
                    press(Shortcut(KeyEvent.KEYCODE_E))

                    press(Shortcut(KeyEvent.KEYCODE_SPACE))

                    press(Shortcut(KeyEvent.KEYCODE_S))
                    press(Shortcut(KeyEvent.KEYCODE_Q))
                    press(Shortcut(KeyEvent.KEYCODE_S))
                    press(Shortcut(KeyEvent.KEYCODE_Q))
                }) {
                    Text("???")
                }
            }

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = {
                    // Étape 1 : Appui sur Ctrl + Echap
                    press(Shortcut(KeyEvent.KEYCODE_ESCAPE, listOf(Shortcut.LEFT_CONTROL)))

                    // Attendre brièvement pour permettre au menu Démarrer de s'ouvrir
                    Thread.sleep(100)

                    // Étape 2 : Taper le nom de l'application
                    val appName = "league of legends"
                    appName.forEach { char ->
                        val keyCode = charToKeyCode(char)
                        press(Shortcut(keyCode))
                        Thread.sleep(100)
                    }

                    // Étape 3 : Appuyer sur Entrée pour lancer l'application
                    press(Shortcut(KeyEvent.KEYCODE_ENTER))

                }) {
                    Text("Lancer : League of legends")
                }
            }

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = {
                    // Étape 1 : Appui sur Ctrl + Echap
                    press(Shortcut(KeyEvent.KEYCODE_ESCAPE, listOf(Shortcut.LEFT_CONTROL)))

                    // Attendre brièvement pour permettre au menu Démarrer de s'ouvrir
                    Thread.sleep(100)

                    // Étape 2 : Taper le nom de l'application
                    val appName = "spotify"
                    appName.forEach { char ->
                        val keyCode = charToKeyCode(char)
                        press(Shortcut(keyCode))
                        Thread.sleep(100)
                    }

                    // Étape 3 : Appuyer sur Entrée pour lancer l'application
                    press(Shortcut(KeyEvent.KEYCODE_ENTER))
                }) {
                    Text("Lancer : Spotify")
                }
            }


        }
}