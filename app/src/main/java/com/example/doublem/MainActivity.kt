package com.example.doublem

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.doublem.ui.theme.DoubleMTheme
import com.example.doublem.composants.bluetooth.BluetoothController
import com.example.doublem.composants.bluetooth.BluetoothDesk
import com.example.doublem.composants.bluetooth.BluetoothUiConnection
import com.example.doublem.composants.bluetooth.ensureBluetoothPermission
import com.example.doublem.data.AppContainer
import com.example.doublem.data.AppDataContainer
import com.example.doublem.ui.darkMode.DarkModeSwitch

// Si vous avez une erreur comme quoi il ne trouve pas les références, faire : Build -> Clean Project
class MainActivity : ComponentActivity() {
    lateinit var container: AppContainer

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var bluetoothController: BluetoothController



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ensureBluetoothPermission(this)
        bluetoothController = BluetoothController()

        container = AppDataContainer(this)

        setContent {
            var isDarkThemeEnabled by remember { mutableStateOf(isDarkModeEnabled(this@MainActivity)) }

            DoubleMTheme(darkTheme = isDarkThemeEnabled) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        DarkModeSwitch(
                            context = this@MainActivity,
                            isDarkThemeEnabled = isDarkThemeEnabled,
                            onThemeChange = { isEnabled ->
                                isDarkThemeEnabled = isEnabled
                            }
                        )

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

fun isDarkModeEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    return prefs.getBoolean("dark_mode", false) // 'false' est la valeur par défaut si 'dark_mode' n'est pas trouvé
}

typealias KeyModifier = Int