package com.laimburggasse.aquariumcontrol.refill

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution

open class RefillRepository(private val mainViewModel: MainViewModel) {
    open suspend fun sendStartRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_START)
    }

    open suspend fun sendStopRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_STOP)
    }

    open suspend fun sendResetRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_RESET)
    }

    private suspend fun executeRequest(apiUrl: String) {
        val webApiPostRequest = WebApiPostRequest()

        webApiPostRequestExecution(
            apiUrl,
            mainViewModel,
            webApiPostRequest,
        )
    }
}