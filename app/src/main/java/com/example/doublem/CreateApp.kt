package com.example.doublem

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CreateApp(navController: NavController) {
    Text("Page de création d'un nouvel élément")

    // Bouton pour revenir à la page principale
    Button(onClick = {
        // Utilisez navController.navigate pour naviguer vers une destination spécifique
        // ou navController.popBackStack pour revenir en arrière dans la pile de navigation
        navController.popBackStack()
    }) {
        Text("Retour")
    }
}