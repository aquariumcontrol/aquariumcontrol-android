package com.laimburggasse.aquariumcontrol.webapi

import com.laimburggasse.aquariumcontrol.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun webApiPostRequestExecution(
    apiUrl: String,
    sessionManager: SessionManager,
    webApiPostRequest: WebApiPostRequest,
    optionalGetParams: Map<String, String> = emptyMap()
): Boolean {
    val user: String? = sessionManager.getControllerUser()
    val password: String? = sessionManager.getControllerPassword()

    if (user == null || password == null) {
        return false
    }
    var url: String = "http://" + sessionManager.getControllerAddress() + apiUrl + "?"

    for ((index: Int, entry: Map.Entry<String, String>) in optionalGetParams.entries.withIndex()) {
        val (key: String, value: String) = entry
        url += "$key=$value"
        if (index != optionalGetParams.size - 1) {
            url += "&"
        }
    }

    return withContext(context = Dispatchers.IO) {
        webApiPostRequest.postData(url, username = user, password).isSuccess
    }
}