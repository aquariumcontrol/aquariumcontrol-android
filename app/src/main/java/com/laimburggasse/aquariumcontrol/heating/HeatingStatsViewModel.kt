package com.laimburggasse.aquariumcontrol.heating

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
class HeatingStatsViewModel @Inject constructor(
    private val heatingStatsRepository: HeatingStatsRepository
): ViewModel() {
    val heatingStatsUiState: StateFlow<HeatingStatsUiState> = heatingStatsRepository.heatingStatsFlow
        .map { result ->
            when(result) {
                is DataFetchResult.Error -> HeatingStatsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    HeatingStatsUiState.Success(heatingStats = result.data)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HeatingStatsUiState.Loading
        )

    fun onHeatingStatsEvent(event: HeatingStatsEvent) {
        when(event) {
            is HeatingStatsEvent.Refresh-> {
                viewModelScope.launch {
                    heatingStatsRepository.triggerRefresh()
                }
            }
        }
    }
}