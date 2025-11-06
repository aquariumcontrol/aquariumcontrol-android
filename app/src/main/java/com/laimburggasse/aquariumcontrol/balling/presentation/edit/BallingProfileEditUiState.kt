package com.laimburggasse.aquariumcontrol.balling.presentation.edit

import com.laimburggasse.aquariumcontrol.balling.domain.BallingProfile

sealed class BallingProfileEditUiState {
    object Loading : BallingProfileEditUiState()
    object NotFound : BallingProfileEditUiState()
    data class Error(val errorMessage: String) : BallingProfileEditUiState()
    data class Success(val ballingProfile: BallingProfile) : BallingProfileEditUiState()
}