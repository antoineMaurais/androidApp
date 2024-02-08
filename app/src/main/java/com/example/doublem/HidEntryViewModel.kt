// HidEntryViewModel.kt
package com.example.doublem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doublem.data.Hid
import com.example.doublem.data.HidsRepository
import kotlinx.coroutines.launch

data class HidDetails(
    val id: Int = 0,
    val name: String = ""
)

data class HidUiState(
    val hidDetails: HidDetails = HidDetails(),
    val isEntryValid: Boolean = false
)

fun HidDetails.toHid(): Hid = Hid(
    id = id,
    name = name,
)
class HidEntryViewModel(private val hidsRepository: HidsRepository) : ViewModel() {

    var hidUiState by mutableStateOf(HidUiState())
        private set

    fun updateUiState(hidDetails: HidDetails) {
        hidUiState =
            HidUiState(hidDetails = hidDetails, isEntryValid = validateInput(hidDetails))
    }

    suspend fun saveHid() {
        if (validateInput()) {
            hidsRepository.insertHid(hidUiState.hidDetails.toHid())
        }
    }

    private fun validateInput(uiState: HidDetails = hidUiState.hidDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }
}
