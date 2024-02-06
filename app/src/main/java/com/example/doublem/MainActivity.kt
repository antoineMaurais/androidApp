package com.example.doublem

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.doublem.ui.theme.DoubleMTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.platform.LocalContext

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ensureBluetoothPermission(this)

        bluetoothController = BluetoothController()

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

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))
//        SearchBar(Modifier.padding(horizontal = 16.dp))
//        HomeSection(title = R.string.align_your_body) {
//            AlignYourBodyRow()
//        }
//        HomeSection(title = R.string.favorite_collections) {
//            FavoriteCollectionsGrid()
//        }
        Text(text = "Hello 2")
        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun AppPortrait() {
    DoubleMTheme (){
        Scaffold(){ padding ->
            HomeScreen(Modifier.padding(padding))
            Text(text = "My app")
        }
    }
}
@Preview(widthDp = 360, heightDp = 640)
@Composable
fun AppPortraitPreview() {
    AppPortrait()
}

//@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
//@Composable
//fun AppElementPreview() {
//    DoubleMTheme {
//        AppElement(
//            text = R.string.ab1_inversions,
//            drawable = R.drawable.ab1_inversions,
//            modifier = Modifier.padding(8.dp)
//        )
//    }
//}


//@Preview(widthDp = 360, heightDp = 640)
//@Composable
//fun SwitchWithCustomColorsPreview() {
//    SwitchWithCustomColors()
//}

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



fun setDarkModeEnabled(context: Context, isEnabled: Boolean) {
    val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        putBoolean("dark_mode", isEnabled)
        apply()
    }
}

fun isDarkModeEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    return prefs.getBoolean("dark_mode", false) // 'false' est la valeur par défaut si 'dark_mode' n'est pas trouvé
}



typealias KeyModifier = Int