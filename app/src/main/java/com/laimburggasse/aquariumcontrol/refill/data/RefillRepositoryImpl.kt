package com.laimburggasse.aquariumcontrol.refill.data

import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.data.remote.webApiPostRequestExecution
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class RefillRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope,
): RefillRepository {
    private val _errorEvents = MutableSharedFlow<String>()
    override val errorEvents = _errorEvents.asSharedFlow()
    override suspend fun sendStartRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_START)
    }

    override suspend fun sendStopRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_POST_CONTROL_STOP)
    }

    override suspend fun sendResetRequest() {
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