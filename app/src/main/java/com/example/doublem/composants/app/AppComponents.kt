package com.example.doublem.composants.app

import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doublem.MainActivity
import com.example.doublem.R
import com.example.doublem.composants.bluetooth.BluetoothInteractionHandler
import com.example.doublem.composants.bluetooth.Shortcut
import com.example.doublem.composants.bluetooth.charToKeyCode
import com.example.doublem.composants.hid.HidEntryViewModel
import com.example.doublem.data.AppViewModelProvider

// Usefull
@Composable
fun AppElementClickable(
    appItem: AppItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bluetoothInteractionHandler: BluetoothInteractionHandler
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
fun AppListWithDAO(
    modifier: Modifier = Modifier,
    bluetoothInteractionHandler: BluetoothInteractionHandler,
    viewModel: HidEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val hids by viewModel.hids.observeAsState(initial = emptyList())
    android.util.Log.i("hid", "hids = "+ hids)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(hids) { hid ->
            val app = AppItem(name = hid.name, icon = R.drawable.ab2_quick_yoga)

            AppElementClickable(app, onClick = {
                onClickImage(app.name, bluetoothInteractionHandler)
            }, bluetoothInteractionHandler = bluetoothInteractionHandler)
        }
    }
}

// Fonction qui s'exécute lorsque l'utilisateur appuye sur une application pour la lancer
fun onClickImage(
    appToStart : String,
    bluetoothInteractionHandler: BluetoothInteractionHandler
){
    android.util.Log.d(MainActivity.TAG, "OnClick image")

    // Étape 1 : Appui sur Ctrl + Echap
    bluetoothInteractionHandler.press(Shortcut(KeyEvent.KEYCODE_ESCAPE, listOf(Shortcut.LEFT_CONTROL)))

    // Attendre brièvement pour permettre au menu Démarrer de s'ouvrir
    Thread.sleep(150)

    // Étape 2 : Taper le nom de l'application
    appToStart.forEach { char ->
        val keyCode = charToKeyCode(char)
        android.util.Log.d("BluetoothHID", "Char: $char for the text: $appToStart")
        bluetoothInteractionHandler.press(Shortcut(keyCode))
        Thread.sleep(100)
    }

    // Étape 3 : Appuyer sur Entrée pour lancer l'application
    bluetoothInteractionHandler.press(Shortcut(KeyEvent.KEYCODE_ENTER))
}