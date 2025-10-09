package com.laimburggasse.aquariumcontrol.balling

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.launch

class BallingViewModel(
    private val ballingRepository: BallingRepository
): ViewModel(), BallingViewModelInterface {

    override var ballingUiState: BallingUiState by mutableStateOf(value = BallingUiState.Loading)
    override var isRefreshing by mutableStateOf(value = false)

    init {
        // initial loading is required so that preview also displays content
        loadData()
    }

    override fun loadData() {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<BallingProfile>> = ballingRepository.fetchBallingProfiles()

            ballingUiState = when(result) {
                is DataFetchResult.Success -> {
                    val ballingProfiles: List<BallingProfile> = result.data
                    if (ballingProfiles.isEmpty()) {
                        BallingUiState.Empty
                    }
                    else {
                        BallingUiState.Success(ballingProfiles)
                    }
                }
                is DataFetchResult.Error -> {
                    BallingUiState.Error(errorMessage = result.errorMessage)
                }
            }

            isRefreshing = false
        }
    }
}