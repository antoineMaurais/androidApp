package com.example.doublem.ui.hid
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.doublem.data.Hid
import com.example.doublem.data.HidsRepository

/**
 * ViewModel to validate and insert hid in the Room database.
 */
class HidEntryViewModel2(private val hidsRepository: HidsRepository) : ViewModel() {

    /**
     * Holds current hid ui state
     */
    var hidUiState by mutableStateOf(HidUiState())
        private set

    /**
     * Updates the [hidUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(hidDetails: HidDetails) {
        hidUiState =
            HidUiState(hidDetails = hidDetails, isEntryValid = validateInput(hidDetails))
    }

    suspend fun saveItem() {
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

/**
 * Represents Ui State for an Hid.
 */
data class HidUiState(
    val hidDetails: HidDetails = HidDetails(),
    val isEntryValid: Boolean = false
)

data class HidDetails(
    val id: Int = 0,
    val name: String = ""
)

/**
 * Extension function to convert [HidDetails] to [Hid].
 */
fun HidDetails.toHid(): Hid = Hid(
    id = id,
    name = name
)

/**
 * Extension function to convert [Hid] to [HidUiState]
 */
fun Hid.toHidUiState(isEntryValid: Boolean = false): HidUiState = HidUiState(
    hidDetails = this.toHidDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Hid] to [HidDetails]
 */
fun Hid.toHidDetails(): HidDetails = HidDetails(
    id = id,
    name = name
)
