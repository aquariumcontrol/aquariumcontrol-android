package com.laimburggasse.aquariumcontrol.balling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BallingProfilesViewModel(
    val ballingProfilesRepository: BallingProfilesRepository
): ViewModel() {

    val ballingProfilesUiState: StateFlow<BallingProfilesUiState> = ballingProfilesRepository.profilesFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> BallingProfilesUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        BallingProfilesUiState.Empty
                    } else {
                        BallingProfilesUiState.Success(ballingProfiles = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BallingProfilesUiState.Loading // The UI starts in a loading state
        )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            ballingProfilesRepository.fetchBallingProfiles()
        }
    }
}