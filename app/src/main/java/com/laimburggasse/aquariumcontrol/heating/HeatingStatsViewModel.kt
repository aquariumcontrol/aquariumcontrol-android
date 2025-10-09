package com.laimburggasse.aquariumcontrol.heating

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

class HeatingStatsViewModel: ViewModel(), HeatingStatsViewModelInterface {
    override var heatingStatsUiState: HeatingStatsUiState by mutableStateOf(value = HeatingStatsUiState.Loading)
    
    override fun loadData(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val result: DataFetchResult<List<HeatingStatsEntry>> = fetchHeatingStats(
                mainViewModel
            )
            // @ todo: transfer content of UiState to other class attributes
            heatingStatsUiState = when(result) {
                is DataFetchResult.Success -> {
                    val heatingStatsList: List<HeatingStatsEntry> = result.data
                    if (heatingStatsList.isEmpty()) {
                        HeatingStatsUiState.Empty
                    }
                    else {
                        HeatingStatsUiState.Success(heatingStats = heatingStatsList)
                    }
                }
                is DataFetchResult.Error -> {
                    HeatingStatsUiState.Error(errorMessage = result.errorMessage)
                }
            }
        }
    }
    private suspend fun fetchHeatingStats(
        mainViewModel: MainViewModel,
    ): DataFetchResult<List<HeatingStatsEntry>> {
        return withContext(context = Dispatchers.IO) {
            val url: String =
                "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_STATS
            val heatingStatsReader = HeatingStatsReader(url)
            val result: DataFetchResult<List<HeatingStatsStringEntry>> = heatingStatsReader.readStatsEntry()
            when (result) {
                is DataFetchResult.Success -> {
                    val heatingStringsStats: List<HeatingStatsStringEntry> = result.data
                    val heatingStats: List<HeatingStatsEntry> = heatingStringsStats.map {
                        it.transformToHeatingStatsEntry()
                    }
                   DataFetchResult.Success(data = heatingStats)
                }
                is DataFetchResult.Error -> {
                    result
                }
            }
        }
    }
}