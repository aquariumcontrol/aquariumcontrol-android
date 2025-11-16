package com.laimburggasse.aquariumcontrol.heating.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
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
                DataFetchResult.Loading -> HeatingStatsUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HeatingStatsUiState.Loading
        )

    fun onHeatingStatsEvent(event: HeatingStatsUiEvent) {
        when(event) {
            is HeatingStatsUiEvent.Refresh-> {
                viewModelScope.launch {
                    heatingStatsRepository.triggerRefresh()
                }
            }
        }
    }
}