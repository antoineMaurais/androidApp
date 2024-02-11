package com.example.doublem.composants.bluetooth

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.doublem.composants.keyboard.KeyboardSender

class BluetoothInteractionHandler(
    private val bluetoothController: BluetoothController,
    private val context: Context
) {
    private val keyboardSender: KeyboardSender

    init {
        val connected = bluetoothController.status as? BluetoothController.Status.Connected
            ?: throw IllegalStateException("Bluetooth not connected")
        keyboardSender = KeyboardSender(connected.btHidDevice, connected.hostDevice)
    }

    fun press(shortcut: Shortcut, releaseModifiers: Boolean = true) {
        @SuppressLint("MissingPermission")
        val result = keyboardSender.sendKeyboard(shortcut.shortcutKey, shortcut.modifiers, releaseModifiers)
        if (!result) Toast.makeText(context, "Can't find keymap for $shortcut", Toast.LENGTH_LONG).show()
    }

}
