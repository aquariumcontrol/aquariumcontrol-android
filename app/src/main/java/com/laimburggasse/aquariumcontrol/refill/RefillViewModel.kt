package com.laimburggasse.aquariumcontrol.refill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.launch

class RefillViewModel: ViewModel() {
    fun sendStartRequest(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val webApiPostRequest = WebApiPostRequest()
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.REFILL_POST_CONTROL_START,
                webApiPostRequest = webApiPostRequest,
            )
        }
    }

    fun sendStopRequest(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val webApiPostRequest = WebApiPostRequest()
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.REFILL_POST_CONTROL_STOP,
                webApiPostRequest = webApiPostRequest,
            )
        }
    }

    fun sendResetRequest(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val webApiPostRequest = WebApiPostRequest()
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.REFILL_POST_CONTROL_RESET,
                webApiPostRequest = webApiPostRequest,
            )
        }
    }

}