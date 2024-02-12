package com.example.doublem.composants.app

import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import com.example.doublem.MainActivity
import com.example.doublem.R
import com.example.doublem.composants.bluetooth.BluetoothInteractionHandler
import com.example.doublem.composants.bluetooth.Shortcut
import com.example.doublem.composants.bluetooth.charToKeyCode
import com.example.doublem.composants.hid.HidEntryViewModel
import com.example.doublem.data.AppViewModelProvider
import com.example.doublem.data.hid.Hid
import kotlinx.coroutines.launch

// Usefull
@Composable
fun AppElementClickable(
    appItem: Hid,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bluetoothInteractionHandler: BluetoothInteractionHandler
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Gestion des clics et maintiens appuyés
    val gestureModifier = modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset: Offset ->
                val startTime = System.currentTimeMillis()
                val wasReleased = tryAwaitRelease()
                val endTime = System.currentTimeMillis()

                // Vérifie si c'est un maintien appuyé (défini ici par plus de 500ms)
                if (endTime - startTime > 500) {
                    // Maintien appuyé
                    showDialog = true
                } else {
                    // Si relâché rapidement, considérer comme un clic simple
                    if (wasReleased) {
                        onClick()
                    }
                }
            }
        )
    }

    Column(
        modifier = gestureModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(R.drawable.ab2_quick_yoga),
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
        Text(
            text = appItem.id.toString(),
            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }

    // Dialogue pour éditer ou supprimer l'application
    if (showDialog) {
        EditAppDialog(
            appItem = appItem, // Assurez-vous que appItem est de type Hid
            onDismiss = { showDialog = false },
            viewModel = viewModel() // Assurez-vous que le ViewModel est accessible ici. Sinon, passez-le comme paramètre.
        )
    }
}

@Composable
fun EditAppDialog(
    appItem: Hid,
    onDismiss: () -> Unit,
    viewModel: HidEntryViewModel
) {
    var text by remember { mutableStateOf(appItem.name) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit App") },
        text = {
            Column {
                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = { Text("App Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val updatedHid = appItem.copy(name = text) // Mettez à jour le nom
                        viewModel.updateHid(updatedHid) // Suppose que vous avez une telle fonction
                        onDismiss()
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deleteHid(appItem)
                        android.util.Log.i("toto","coroutineScope deleteHid hid = (${appItem.id}) and name = (${appItem.name})")
                        onDismiss()
                    }
                }
            ) { Text("Delete") }
        }
    )
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
            android.util.Log.d("tata", "hid.id = ${hid.id}")

            AppElementClickable(hid, onClick = {
                onClickImage(hid.name, bluetoothInteractionHandler)
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