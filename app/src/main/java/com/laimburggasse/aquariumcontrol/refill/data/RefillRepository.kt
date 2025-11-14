package com.laimburggasse.aquariumcontrol.refill.data

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class RefillRepository @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope,
) {
    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()
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
            val requestSuccess: Boolean = webApiPostRequestExecution(
                apiUrl,
                sessionManager,
                webApiPostRequest,
            )
            if (!requestSuccess) {
                _errorEvents.emit(value = "Request failed. Please check connection and server configuration and try again.")
            }
        }
    }
}