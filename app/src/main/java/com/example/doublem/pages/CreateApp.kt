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
import androidx.navigation.NavController

@Composable
fun CreateApp(navController: NavController) {
    // Utilisation de CreateAppViewModel pour gérer l'état
//    val createAppViewModel: CreateAppViewModel = viewModel()
    var appName by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text("Annuler")
            }
//            Button(onClick = {
//                createAppViewModel.insertHid(appName)
////                navController.popBackStack()
//                // Logique pour ajouter une nouvelle application avec le nom et l'image
//            }) {
//                Text("Ajouter")
//            }
        }
    }
}