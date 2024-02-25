// CreateApp.kt
package com.example.doublem.pages

import android.content.ContentResolver
import android.content.Context
import android.media.Image
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.doublem.data.AppViewModelProvider
import com.example.doublem.composants.hid.HidEntryViewModel
import com.example.doublem.navigation.AppDestinations
import kotlinx.coroutines.launch

@Composable
fun CreateApp(
    navController: NavController,
    viewModel: HidEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var appName by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String>("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri: Uri? ->
//            imageUri = uri.toString()
//        }
        onResult = { newUri: Uri? ->
            if (newUri == null) return@rememberLauncherForActivityResult

            val input = context.contentResolver.openInputStream(newUri) ?: return@rememberLauncherForActivityResult
            val outputFile = context.filesDir.resolve(appName)
            input.copyTo(outputFile.outputStream())
            outputFile.toUri()
        }
    )



    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Text("Add a new application")
        TextField(
            value = appName,
            onValueChange = { appName = it },
            label = { Text("Application name") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        // Add a button to open the media picker
        Button(onClick = { mediaPickerLauncher.launch("image/*") }) {
            Text("Pick an image")
        }

        Spacer(Modifier.height(16.dp))

        // Display the picked image
        imageUri?.let { uri ->

            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Picked image",
                modifier = Modifier.size(128.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Cancel")
            }
            Button(onClick = {
                if (appName.isNotEmpty()) {

                    coroutineScope.launch {
                        viewModel.saveHid(appName, imageUri)
                        // Utilisez la méthode navigate pour revenir à l'écran d'accueil, en vous assurant d'utiliser le bon ID ou la bonne route.
                        navController.navigate(AppDestinations.MAIN_SCREEN) {
                            // Cela effacera la pile de navigation jusqu'à l'écran d'accueil et ne gardera pas l'écran actuel dans la pile.
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }

                }
            }) {
                Text("Add")
            }
        }
    }
}