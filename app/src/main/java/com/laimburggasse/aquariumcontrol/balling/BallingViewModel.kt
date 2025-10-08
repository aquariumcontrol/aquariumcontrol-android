package com.laimburggasse.aquariumcontrol.balling

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

class BallingViewModel: ViewModel(), BallingViewModelInterface {

    override var ballingUiState: BallingUiState by mutableStateOf(value = BallingUiState.Loading)

    override var isRefreshing by mutableStateOf(value = false)

    override fun loadData(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<BallingProfile>> = fetchBallingProfiles(mainViewModel)

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
    
    suspend fun fetchBallingProfiles(mainViewModel: MainViewModel): DataFetchResult<List<BallingProfile>> {
        val url = "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_SET_VALS
        val reader = BallingProfileReader(url)
        return withContext(Dispatchers.IO) {
            reader.readBallingProfiles()
        }
    }
}