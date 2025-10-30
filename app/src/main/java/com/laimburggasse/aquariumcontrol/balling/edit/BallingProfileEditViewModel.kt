package com.laimburggasse.aquariumcontrol.balling.edit

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.balling.common.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.common.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BallingProfileEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ballingProfilesRepository: BallingProfilesRepository,
): ViewModel() {
    // This class will allow the user to edit the Balling dosing profile.

    var ballingProfileEditUiState: BallingProfileEditUiState by mutableStateOf(
        value = BallingProfileEditUiState.Loading
    )
        private set

    init {
        viewModelScope.launch {
            // 1. Get the profileId from navigation arguments via SavedStateHandle.
            val ballingProfileId: Int? = savedStateHandle.get("ballingProfileId")

            if (ballingProfileId != null) {
                val currentRepoState: DataFetchResult<List<BallingProfile>> =
                    ballingProfilesRepository.ballingProfiles.first()

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
            else {
                // @ todo: emit error state
                Log.d("BallingProfileEditViewModel", "ballingProfileId is null")
            }
        }
    }

    fun onConfirmDosing(newVolume: Double) {
        val currentState: BallingProfileEditUiState = ballingProfileEditUiState
        if (currentState is BallingProfileEditUiState.Success) {
            val updatedProfile: BallingProfile = currentState.ballingProfile.copy(volume = newVolume)

            ballingProfileEditUiState = BallingProfileEditUiState.Success(ballingProfile = updatedProfile)

            viewModelScope.launch {
                ballingProfilesRepository.updateBallingProfile(ballingProfile = updatedProfile)
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

    fun onEvent(event: BallingProfileEditEvent) {
        when (event) {
            is BallingProfileEditEvent.ConfirmDosingVolume -> {
                onConfirmDosing(newVolume = event.newDosingValue)
            }
            is BallingProfileEditEvent.ChangeDosingVolume -> {
                onVolumeChange(newVolume = event.newDosingValue)
            }
        }
    }
}