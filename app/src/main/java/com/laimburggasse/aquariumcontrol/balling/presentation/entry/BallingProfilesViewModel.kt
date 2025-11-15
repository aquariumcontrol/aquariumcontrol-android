package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.balling.data.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BallingProfilesViewModel @Inject constructor(
    private val ballingProfilesRepository: BallingProfilesRepository
): ViewModel() {

    val ballingProfilesUiState: StateFlow<BallingProfilesUiState> = ballingProfilesRepository.ballingProfiles
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
                is DataFetchResult.Loading -> BallingProfilesUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BallingProfilesUiState.Loading // The UI starts in a loading state
        )

   fun onRefresh() {
        viewModelScope.launch {
            ballingProfilesRepository.triggerRefresh()
        }
    }
}