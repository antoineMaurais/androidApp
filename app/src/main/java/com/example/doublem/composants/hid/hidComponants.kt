package com.example.doublem.composants.hid

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doublem.data.Hid
import com.example.doublem.ui.AppViewModelProvider
import com.example.doublem.ui.hid.HidEntryViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun HidListScreen(viewModel: HidEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val hids by viewModel.hids.observeAsState(initial = emptyList())
    Log.i("hid", "hids = "+ hids)
    LazyColumn {
        items(hids) { hid ->
            HidItem(hid)
        }
    }
}

@Composable
fun HidItem(hid: Hid) {
    Text(text = "Application name: ${hid.name}")
    // Vous pouvez ajouter plus de détails ou de la mise en page ici
}