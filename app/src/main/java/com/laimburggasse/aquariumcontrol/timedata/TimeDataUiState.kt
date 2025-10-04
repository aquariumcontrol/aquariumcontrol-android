package com.laimburggasse.aquariumcontrol.timedata

sealed class TimeDataUiState {
    object Loading : TimeDataUiState()
    data class Success(val timeDataSets: TimeDataSets) : TimeDataUiState()
    data class Error(val errorMessage: String) : TimeDataUiState()
}