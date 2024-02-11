package com.example.doublem.composants.hid
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.doublem.data.hid.Hid
import com.example.doublem.data.hid.HidsRepository

class HidEntryViewModel(private val hidsRepository: HidsRepository) : ViewModel() {
    var hidUiState by mutableStateOf(HidUiState())
        private set

    fun updateUiState(hidDetails: HidDetails) {
        hidUiState =
            HidUiState(hidDetails = hidDetails, isEntryValid = validateInput(hidDetails))
    }

    val hids: LiveData<List<Hid>> = hidsRepository.getAllHidsStream().asLiveData()


    suspend fun saveHid(name: String) {
        if (!validateInput()) {
            val newHid = Hid(name = name)
            hidsRepository.insertHid(newHid)
//            val hid = hidsRepository.insertHid(hidUiState.hidDetails.toHid())
            Log.i("toto","hid = ($newHid)")
        }
    }

    private fun validateInput(uiState: HidDetails = hidUiState.hidDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }
}

data class HidUiState(
    val hidDetails: HidDetails = HidDetails(),
    val isEntryValid: Boolean = false
)

data class HidDetails(
    val id: Int = 0,
    val name: String = ""
)

fun HidDetails.toHid(): Hid = Hid(
    id = id,
    name = name
)

fun Hid.toHidUiState(isEntryValid: Boolean = false): HidUiState = HidUiState(
    hidDetails = this.toHidDetails(),
    isEntryValid = isEntryValid
)

fun Hid.toHidDetails(): HidDetails = HidDetails(
    id = id,
    name = name
)
