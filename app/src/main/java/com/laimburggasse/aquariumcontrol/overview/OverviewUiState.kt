package com.laimburggasse.aquariumcontrol.overview

sealed class OverviewUiState {
    object Loading : OverviewUiState()
    data class Success(val refillHistory: OverviewData) : OverviewUiState()
    data class Error(val errorMessage: String) : OverviewUiState()
}