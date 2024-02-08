// CreateApp.kt
package com.example.doublem.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doublem.HidEntryViewModel
import com.example.doublem.data.Hid
import com.example.doublem.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun CreateApp(
    navController: NavController,
    viewModel: HidEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Utilisation de CreateAppViewModel pour gérer l'état
//    val createAppViewModel: CreateAppViewModel = viewModel()
    var appName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Text("Ajouter une nouvelle application")
        TextField(
            value = appName,
            onValueChange = { appName = it },
            label = { Text("Nom de l'application") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Annuler")
            }
            Button(onClick = {
                if (appName.isNotEmpty()) {
                    try {
                        coroutineScope.launch {
                            viewModel.saveHid()
//                      navController.popBackStack()
                        }
                    } catch(e : IOException) {
                        e.printStackTrace()
                    }
                }
            }) {
                Text("Ajouter")
            }
        }
    }
}