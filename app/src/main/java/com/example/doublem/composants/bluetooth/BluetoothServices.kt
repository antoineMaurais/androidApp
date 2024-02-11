package com.example.doublem.composants.bluetooth

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.doublem.MainActivity

fun ensureBluetoothPermission(activity: ComponentActivity) {
    val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
        if (isGranted) {
            Log.d(MainActivity.TAG, "Bluetooth connection granted")
        } else { Log.e(MainActivity.TAG, "Bluetooth connection not granted, Bye!")
            activity.finish()
        }
    }

    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
    }
}