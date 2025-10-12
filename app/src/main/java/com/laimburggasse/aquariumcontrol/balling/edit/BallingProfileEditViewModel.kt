package com.laimburggasse.aquariumcontrol.balling.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.balling.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.launch

class BallingProfileEditViewModel(
    ballingProfileId: Int,
    private val repository: BallingProfilesRepository,
) : ViewModel() {
    // This class will allow the user to edit the Balling dosing profile.
    var ballingProfileEditUiState: BallingProfileEditUiState by mutableStateOf(
        value = BallingProfileEditUiState.Loading
    )
        private set

    init {
        val currentRepoState: DataFetchResult<List<BallingProfile>> = repository.profilesFlow.value

        if (currentRepoState is DataFetchResult.Success) {
            val profile: BallingProfile? = currentRepoState.data.find {
                it.pumpId == ballingProfileId
            }
            ballingProfileEditUiState = if (profile != null) {
                BallingProfileEditUiState.Success(ballingProfile = profile)
            } else {
                BallingProfileEditUiState.NotFound
            }
        } else {
            ballingProfileEditUiState = BallingProfileEditUiState.Error(
                errorMessage = "Data not available. Go back and try again."
            )
        }
    }

    fun onConfirmDosing(newVolume: Double) {
        val currentState: BallingProfileEditUiState = ballingProfileEditUiState
        if (currentState is BallingProfileEditUiState.Success) {
            val updatedProfile = currentState.ballingProfile.copy(volume = newVolume)

            ballingProfileEditUiState = BallingProfileEditUiState.Success(updatedProfile)

            viewModelScope.launch {
                repository.updateBallingProfile(ballingProfile = updatedProfile)
            }
        }
    }

    fun onVolumeChange(newVolume: Double) {
        val currentState = ballingProfileEditUiState
        if (currentState is BallingProfileEditUiState.Success) {
            ballingProfileEditUiState = currentState.copy(
                ballingProfile = currentState.ballingProfile.copy(volume = newVolume)
            )
        }
    }
}