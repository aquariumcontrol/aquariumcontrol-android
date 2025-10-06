package com.laimburggasse.aquariumcontrol.overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel: ViewModel(), OverviewViewModelInterface {
    // Holds the current state of the overview screen
    override var overviewUiState: OverviewUiState by mutableStateOf(OverviewUiState.Loading)
        private set

    // Manages the refreshing state for the PullToRefreshBox
    override var isRefreshing by mutableStateOf(false)
        private set

    override fun loadData(mainViewModel: MainViewModel) {
        // Use viewModelScope which is automatically cancelled when the ViewModel is cleared
        viewModelScope.launch {
            isRefreshing = true

            val result = fetchOverviewData(mainViewModel)

            overviewUiState = when (result) {
                is DataFetchResult.Error -> OverviewUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> OverviewUiState.Success(overviewData = result.data)
            }
            // Always set refreshing to false when the operation is complete
            isRefreshing = false
        }
    }

    private suspend fun fetchOverviewData(mainViewModel: MainViewModel): DataFetchResult<OverviewData> {
        return withContext(Dispatchers.IO) {
            val url =
                "http://" + mainViewModel.getControllerAddress() + WebApiUrls.OVERVIEW_GET_DATA
            val reader = OverviewDataReader(url)
            reader.readOverviewData()
        }
    }
}