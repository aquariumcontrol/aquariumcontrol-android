package com.laimburggasse.aquariumcontrol.timedata.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.timedata.data.TimeDataRepository
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeDataViewModel @Inject constructor(
    private val timeDataRepository: TimeDataRepository
) : ViewModel() {
    val timeDataUiState: StateFlow<TimeDataUiState> = timeDataRepository.timeDataSamples
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> TimeDataUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        TimeDataUiState.Empty
                    } else {
                        TimeDataUiState.Success(timeDataSets = TimeDataSets(timeData = result.data))
                    }
                }
                DataFetchResult.Loading -> TimeDataUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TimeDataUiState.Loading
        )


    fun onTimeDataEvent(event: TimeDataUiEvent) {
        when (event) {
            is TimeDataUiEvent.Refresh -> {
                viewModelScope.launch {
                    timeDataRepository.triggerRefresh()
                }
            }
        }
    }
}