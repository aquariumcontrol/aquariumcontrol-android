package com.laimburggasse.aquariumcontrol.overview.presentation

import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData

sealed class OverviewUiState {
    object Loading : OverviewUiState()
    data class Success(val overviewData: OverviewData) : OverviewUiState()
    data class Error(val errorMessage: String) : OverviewUiState()
}