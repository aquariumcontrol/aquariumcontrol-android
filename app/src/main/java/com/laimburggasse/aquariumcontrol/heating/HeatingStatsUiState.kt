package com.laimburggasse.aquariumcontrol.heating

sealed class HeatingStatsUiState {
    object Loading : HeatingStatsUiState()
    object Empty: HeatingStatsUiState()
    data class Success(val heatingStats: List<HeatingStatsEntry>) : HeatingStatsUiState()
    data class Error(val errorMessage: String) : HeatingStatsUiState()
}