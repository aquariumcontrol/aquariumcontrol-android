package com.laimburggasse.aquariumcontrol.overview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.overview.domain.repository.OverviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val overviewRepository: OverviewRepository
): ViewModel() {
    val overviewUiState: StateFlow<OverviewUiState> = overviewRepository.overviewData
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> OverviewUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    OverviewUiState.Success(overviewData = result.data)
                }
                DataFetchResult.Loading -> OverviewUiState.Loading
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

    fun onOverviewEvent(event: OverviewUiEvent) {
        when (event) {
            is OverviewUiEvent.Refresh -> {
                loadData()
            }
        }
    }
}