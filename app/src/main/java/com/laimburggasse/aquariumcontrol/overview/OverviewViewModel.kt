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
    override var overviewUiState: OverviewUiState by mutableStateOf(OverviewUiState.Loading)
        private set

    override var isRefreshing by mutableStateOf(value = false)
        private set

    override fun loadData(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<OverviewData> = fetchOverviewData(mainViewModel)

            overviewUiState = when (result) {
                is DataFetchResult.Error -> OverviewUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> OverviewUiState.Success(overviewData = result.data)
            }
            isRefreshing = false
        }
    }

    private suspend fun fetchOverviewData(mainViewModel: MainViewModel): DataFetchResult<OverviewData> {
        return withContext(context = Dispatchers.IO) {
            val url =
                "http://" + mainViewModel.getControllerAddress() + WebApiUrls.OVERVIEW_GET_DATA
            val reader = OverviewDataReader(url)
            reader.readOverviewData()
        }
    }
}