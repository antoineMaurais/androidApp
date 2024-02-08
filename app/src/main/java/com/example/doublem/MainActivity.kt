package com.example.doublem

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.doublem.ui.theme.DoubleMTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doublem.data.AppContainer
import com.example.doublem.data.AppDataContainer
import com.example.doublem.pages.CreateApp

//import com.example.doublem.ui.AppDestinations

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var bluetoothController: BluetoothController

    private fun ensureBluetoothPermission(activity: ComponentActivity) {
        val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted: Boolean ->
            if (isGranted) {Log.d(MainActivity.TAG, "Bluetooth connection granted")
            } else { Log.e(MainActivity.TAG, "Bluetooth connection not granted, Bye!")
                activity.finish()
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    lateinit var container: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ensureBluetoothPermission(this)

        bluetoothController = BluetoothController()


        container = AppDataContainer(this)

        setContent {
            // Utilisez 'var' pour que la variable puisse être réaffectée
            var isDarkThemeEnabled by remember { mutableStateOf(isDarkModeEnabled(this@MainActivity)) }
            // Utilisez 'this@MainActivity' pour obtenir une référence au contexte de MainActivity

            DoubleMTheme(darkTheme = isDarkThemeEnabled) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // Utilisez Row pour aligner le contenu en haut de l'écran
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() // Prendre toute la largeur disponible
                                .padding(16.dp), // Ajouter un peu de marge autour de la barre supérieure
                            horizontalArrangement = Arrangement.End, // Aligner le contenu à la fin (droite)
                            verticalAlignment = Alignment.CenterVertically // Centrer verticalement les éléments dans la Row
                        ) {
                            // Texte indiquant que le switch est pour le mode sombre
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // Utilisez Column pour aligner verticalement, si nécessaire pour d'autres éléments
                            Column(
                                horizontalAlignment = Alignment.End // Aligner le contenu horizontalement à la fin (droite)
                            ) {

                                // Utilisez le switch pour changer le mode sombre
                                SwitchWithCustomColors(
                                    checked = isDarkThemeEnabled,
                                    onCheckedChange = { isEnabled ->
                                        // Réaffectez la nouvelle valeur à la variable
                                        isDarkThemeEnabled = isEnabled
                                        // Enregistrez le nouveau choix de thème dans SharedPreferences
                                        setDarkModeEnabled(this@MainActivity, isEnabled)
                                        // Vous devrez peut-être reconstruire l'interface utilisateur pour que le changement de thème prenne effet
                                    }
                                )
                            }
                        }

                        BluetoothUiConnection(bluetoothController)
                        BluetoothDesk(bluetoothController)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        bluetoothController.release()
    }
 }

// Page principale une fois la connexion bluetooth établit
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth() // Prendre toute la largeur disponible
                .padding(16.dp), // Ajouter un peu de marge autour de la barre supérieure
            horizontalArrangement = Arrangement.End, // Aligner le contenu à la fin (droite)
            verticalAlignment = Alignment.CenterVertically // Centrer verticalement les éléments dans la Row
        ) {
            // Je sais pas trop pourquoi ça marche mais quand je fais, c'est aligné
        }
        
        Text(text = "DoubleM app's!")

        Button(onClick = { navController.navigate(AppDestinations.CREATE_ELEMENT_SCREEN) }) {
            Text("Ajouter un élément")
        }

        Spacer(Modifier.height(16.dp))
    }
}

// Applique le thème de l'application à l'ensemble de l'application
@Composable
fun AppPortrait(navController: NavController) {
    DoubleMTheme (){
        Scaffold(){ padding ->
            HomeScreen(Modifier.padding(padding), navController)
        }
    }
}

// (Related to the dark mode) Switch button
@Composable
fun SwitchWithCustomColors(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}


// (Related to the dark mode)
fun setDarkModeEnabled(context: Context, isEnabled: Boolean) {
    val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        putBoolean("dark_mode", isEnabled)
        apply()
    }
}

// (Related to the dark mode)
fun isDarkModeEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    return prefs.getBoolean("dark_mode", false) // 'false' est la valeur par défaut si 'dark_mode' n'est pas trouvé
}

// List des chemins de navigation
object AppDestinations {
    const val MAIN_SCREEN = "main"
    const val CREATE_ELEMENT_SCREEN = "createElement"
}

// Navigation de l'application
@Composable
fun AppNavigation(): NavHostController {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.MAIN_SCREEN) {
        // Page principale
        composable(AppDestinations.MAIN_SCREEN) {
            AppPortrait(navController = navController)
        }
        // Page d'ajout d'applicaiton à lancer
        composable(AppDestinations.CREATE_ELEMENT_SCREEN) {
            CreateApp(navController = navController)
        }
    }
    return navController
}



typealias KeyModifier = Int