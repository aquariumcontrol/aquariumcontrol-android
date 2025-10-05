package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryDosingEvent
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryUiState

sealed class BallingUiState {
    object Loading : BallingUiState()
    data class Success(val ballingProfiles: List<BallingProfile>) : BallingUiState()
    data class Error(val errorMessage: String) : BallingUiState()
}