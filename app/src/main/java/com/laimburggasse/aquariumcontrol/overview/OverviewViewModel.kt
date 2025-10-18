package com.laimburggasse.aquariumcontrol.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OverviewViewModel(
    private val overviewRepository: OverviewRepository
): ViewModel() {
    val overviewUiState: StateFlow<OverviewUiState> = overviewRepository.overviewData
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> OverviewUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    OverviewUiState.Success(overviewData = result.data)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = OverviewUiState.Loading
        )

    fun loadData() {
        viewModelScope.launch {
            overviewRepository.fetchOverviewData()
        }
    }

    init {
        loadData()
    }
}