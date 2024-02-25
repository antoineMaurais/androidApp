package com.example.doublem.composants.hid
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.doublem.R
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

    suspend fun saveHid(name: String, imageUri: String) {
        if (name.isNotBlank()) {
            val newHid = Hid(name = name, imageUri= imageUri)
            val insertedId = hidsRepository.insertHid(newHid)
            Log.i("toto","saveHid hid = (${insertedId})")
        }
    }

    private fun validateInput(uiState: HidDetails = hidUiState.hidDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }

    suspend fun deleteHid(hid: Hid) {
        Log.i("toto","deleteHid hid = (${hid.id}) and name = (${hid.name})")
        hidsRepository.deleteHid(hid)
    }

    suspend fun updateHid(updateHid: Hid) {
        if (!validateInput()) {
            hidsRepository.updateHid(updateHid)
            Log.i("toto","updateHid hid = (${updateHid.id}) and name = (${updateHid.name})")
        }
    }
}

data class HidUiState(
    val hidDetails: HidDetails = HidDetails(),
    val isEntryValid: Boolean = false
)

data class HidDetails(
    val id: Int = 0,
    val name: String = "",
//    val imageResId: Int = R.drawable.ab1_inversions // Identifiant de la ressource drawable par défaut
    val imgUri: String = "content://media/picker/0/com.android.providers.media.photopicker/media/1000010397"
)

fun HidDetails.toHid(): Hid = Hid(
    id = id,
    name = name,
    imageUri = imgUri
)

fun Hid.toHidUiState(isEntryValid: Boolean = false): HidUiState = HidUiState(
    hidDetails = this.toHidDetails(),
    isEntryValid = isEntryValid
)

fun Hid.toHidDetails(): HidDetails = HidDetails(
    id = id,
    name = name
)
