package com.laimburggasse.aquariumcontrol.balling.entry

import com.laimburggasse.aquariumcontrol.balling.common.BallingProfile

sealed class BallingProfilesUiState {
    object Empty : BallingProfilesUiState()
    object Loading : BallingProfilesUiState()
    data class Success(val ballingProfiles: List<BallingProfile>) : BallingProfilesUiState()
    data class Error(val errorMessage: String) : BallingProfilesUiState()
}