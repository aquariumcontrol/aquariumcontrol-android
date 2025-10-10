package com.laimburggasse.aquariumcontrol.balling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BallingViewModel(
    val ballingRepository: BallingRepository
): ViewModel() {

    val ballingUiState: StateFlow<BallingUiState> = ballingRepository.profilesFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> BallingUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        BallingUiState.Empty
                    } else {
                        BallingUiState.Success(ballingProfiles = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BallingUiState.Loading // The UI starts in a loading state
        )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            ballingRepository.fetchBallingProfiles()
        }
    }
}