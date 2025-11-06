package com.laimburggasse.aquariumcontrol.timedata.presentation

sealed class TimeDataUiState {
    object Loading : TimeDataUiState()
    object Empty : TimeDataUiState()
    data class Success(val timeDataSets: TimeDataSets) : TimeDataUiState()
    data class Error(val errorMessage: String) : TimeDataUiState()
}