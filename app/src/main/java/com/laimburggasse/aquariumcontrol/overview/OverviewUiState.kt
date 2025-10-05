package com.laimburggasse.aquariumcontrol.overview

sealed class OverviewUiState {
    object Loading : OverviewUiState()
    data class Success(val overviewData: OverviewData) : OverviewUiState()
    data class Error(val errorMessage: String) : OverviewUiState()
}