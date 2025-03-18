package com.laimburggasse.aquariumcontrol.webapi

import android.content.Context
import android.widget.Toast
import com.laimburggasse.aquariumcontrol.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun webApiPostRequestExecution(
    apiUrl: String,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    webApiPostRequest: WebApiPostRequest,
    context: Context,
    optionalGetParams: Map<String, String> = emptyMap()
) {
    val user = mainViewModel.getControllerUser()
    val password = mainViewModel.getControllerPassword()

    if (user != null && password != null) {
        var url =
            "http://" + mainViewModel.getControllerAddress() + apiUrl + "?"

        for ((index, entry) in optionalGetParams.entries.withIndex()) {
            val (key, value) = entry
            url += "$key=$value"
            if (index != optionalGetParams.size - 1) {
                url += "&"
            }
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                webApiPostRequest.postData(url, user, password)
            }
        }
    } else {
        Toast.makeText(
            context,
            "Currently selected controller does not have " +
                    "proper configuration of user and password",
            Toast.LENGTH_SHORT
        ).show()
    }
}