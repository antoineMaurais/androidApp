package com.example.doublem

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.view.KeyEvent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.navigation.NavController
import com.example.bluetoothsample.KeyboardSender

data class Shortcut( val shortcutKey: Int,
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

// Interface pour ce connecter à un appareil en bluetooth
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

    // Fonction qui envoie un shortcut HID à l'ordinateur
    fun press(shortcut: Shortcut, releaseModifiers: Boolean = true) {
        @SuppressLint("MissingPermission")
        val result = keyboardSender.sendKeyboard(shortcut.shortcutKey, shortcut.modifiers, releaseModifiers)
        if (!result) Toast.makeText(context,"can't find keymap for $shortcut",Toast.LENGTH_LONG).show()
    }

    // Fonction qui s'exécute lorsque l'utilisateur appuye sur une application pour la lancer
    fun onClickImage(appToStart : String){
        android.util.Log.d(MainActivity.TAG, "OnClick image")

        // Étape 1 : Appui sur Ctrl + Echap
        press(Shortcut(KeyEvent.KEYCODE_ESCAPE, listOf(Shortcut.LEFT_CONTROL)))

        // Attendre brièvement pour permettre au menu Démarrer de s'ouvrir
        Thread.sleep(100)

        // Étape 2 : Taper le nom de l'application
        appToStart.forEach { char ->
            val keyCode = charToKeyCode(char)
            android.util.Log.d("BluetoothHID", "Char: $char for the text: $appToStart")
            press(Shortcut(keyCode))
            Thread.sleep(100)
        }

        // Étape 3 : Appuyer sur Entrée pour lancer l'application
        press(Shortcut(KeyEvent.KEYCODE_ENTER))
    }

    // Interface pour les applications affiché dans la liste des applications
    data class AppItem(
        val name: String,
        @DrawableRes val icon: Int
    )

    val appList = listOf(
        AppItem(name = "spotify", icon = R.drawable.ab2_quick_yoga),
        AppItem(name = "league of legends", icon = R.drawable.ab1_inversions),
        AppItem(name = "discord", icon = R.drawable.ab1_inversions)
        // Ajoutez d'autres applications ici
    )

    // Usefull
    @Composable
    fun AppElementClickable(
        appItem: AppItem,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(appItem.icon),
                contentDescription = appItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
            )
            Text(
                text = appItem.name,
                modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    @Composable
    fun AppList(
        modifier: Modifier = Modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = modifier
        ) {
            items(appList) { item ->
                AppElementClickable(item, onClick = {
                    onClickImage(item.name)
                })
            }
        }
    }

    // Affichage une fois la connection bluetooth réalisé
    Column( modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {

        Spacer(Modifier.height(16.dp))

        AppList()   // Affiche la liste des applications lanceable

        AppNavigation() // Modifier appDashboard.kt pour modifer la page relié à ce bouton, ou modifier le composant HomeScreen qui est appelé dans AppNavigation dans le fichier MainActivity

        Spacer(Modifier.height(16.dp))

//        Text("Slide Desk")
//        Spacer(modifier = Modifier.size(10.dp))
//
//        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
//            Button(onClick = { press(Shortcut(KeyEvent.KEYCODE_DPAD_LEFT)) }) {
//                Text("<-")
//            }
//            Spacer(modifier = Modifier.size(20.dp))
//            Button(onClick = { press(Shortcut(KeyEvent.KEYCODE_DPAD_RIGHT)) }) {
//                Text("->")
//            }
//        }

    }
}