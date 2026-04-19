/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.balling.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BallingProfileEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ballingProfilesRepository: BallingProfilesRepository,
): ViewModel() {
    // This class will allow the user to edit the Balling dosing profile.

    val unconfirmedProfile = MutableStateFlow<BallingProfile?>(value = null)

    var ballingProfileEditUiState: StateFlow<BallingProfileEditUiState> =
        combine(
            flow = ballingProfilesRepository.ballingProfiles,
            flow2 = unconfirmedProfile
        ) { result, unconfirmedProfile ->
            // Get the ID from navigation arguments.
            val ballingProfileId: Int = savedStateHandle["ballingProfileId"]
                ?: return@combine BallingProfileEditUiState.Error(
                    errorMessage = GlobalConstants.ERR_MSG_PROFILE_ID_NOT_PROVIDED
                )

            when (result) {
                is DataFetchResult.Success -> {
                    // Find the specific profile from the list.
                    val profileFromServer: BallingProfile? = result.data.find {
                        it.pumpId == ballingProfileId
                    }
                    if (profileFromServer != null) {
                        // 2. Prioritize the unconfirmed profile if it exists.
                        //    If the user has started editing, show their edits.
                        //    Otherwise, show the profile from the server.
                        val profileToShow: BallingProfile = unconfirmedProfile ?: profileFromServer
                        BallingProfileEditUiState.Success(ballingProfile = profileToShow)
                    } else {
                        // The list was loaded, but the specific profile wasn't found.
                        BallingProfileEditUiState.NotFound
                    }
                }
                is DataFetchResult.Error -> BallingProfileEditUiState.Error(
                    errorMessage = result.errorMessage
                )
                is DataFetchResult.Loading -> BallingProfileEditUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = BallingProfileEditUiState.Loading // Start in a loading state.
        )

    fun onConfirmDosing(newVolume: Double) {
        val currentState: BallingProfileEditUiState = ballingProfileEditUiState.value
        if (currentState is BallingProfileEditUiState.Success) {
            val updatedProfile: BallingProfile = currentState.ballingProfile.copy(volume = newVolume)
            viewModelScope.launch {
                ballingProfilesRepository.updateBallingProfile(ballingProfile = updatedProfile)
            }
        }
    }

    /**
     * Updates the `unconfirmedProfile` StateFlow with the user's edits.
     * This will trigger the `combine` operator and update the UI state reactively.
     */
    fun onVolumeChange(newVolume: Double) {
        // Read the current state of the UI
        val currentState: BallingProfileEditUiState = ballingProfileEditUiState.value

        // Only proceed if we have a successful state to work with
        if (currentState is BallingProfileEditUiState.Success) {
            // If the user hasn't edited before, `unconfirmedProfile` is null.
            // In that case, we start with a copy of the profile from the UI state.
            // Otherwise, we continue editing the existing `unconfirmedProfile`.
            val baseProfile: BallingProfile = unconfirmedProfile.value ?: currentState.ballingProfile

            // Create a new copy with the updated volume and set it.
            unconfirmedProfile.value = baseProfile.copy(volume = newVolume)
        }
    }

    fun onEvent(event: BallingProfileEditUiEvent) {
        when (event) {
            is BallingProfileEditUiEvent.ConfirmDosingVolume -> {
                onConfirmDosing(newVolume = event.newDosingValue)
            }
            is BallingProfileEditUiEvent.ChangeDosingVolume -> {
                onVolumeChange(newVolume = event.newDosingValue)
            }
        }
    }
}