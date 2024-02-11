package com.example.doublem.ui.darkMode

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun DarkModeSwitch(
    context: Context,
    isDarkThemeEnabled: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dark Mode",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.padding(5.dp))
        Column(horizontalAlignment = Alignment.End) {
            SwitchWithCustomColors(
                checked = isDarkThemeEnabled,
                onCheckedChange = { isEnabled ->
                    onThemeChange(isEnabled)
                    setDarkModeEnabled(context, isEnabled)
                }
            )
        }
    }
}

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

