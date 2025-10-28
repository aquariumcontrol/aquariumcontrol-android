package com.laimburggasse.aquariumcontrol.timedata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
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
                        TimeDataUiState.Success(
                            timeDataSets = TimeDataSets(timeData = result.data)
                        )
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TimeDataUiState.Loading
        )

    fun loadData() {
        viewModelScope.launch {
            timeDataRepository.fetchTimeData()
        }
    }

    init {
        loadData()
    }

    fun onTimeDataEvent(event: TimeDataEvent) {
        when (event) {
            is TimeDataEvent.Refresh -> loadData()
        }
    }
}