package com.laimburggasse.aquariumcontrol.refill.presentation.entry

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

open class RefillRepository @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope,
) {
    open suspend fun sendStartRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_START)
    }

    open suspend fun sendStopRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_STOP)
    }

    open suspend fun sendResetRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_RESET)
    }

    private fun executeRequest(apiUrl: String) {
        val webApiPostRequest = WebApiPostRequest()

        repositoryScope.launch {
            webApiPostRequestExecution(
                apiUrl,
                sessionManager,
                webApiPostRequest,
            )
        }
    }
}