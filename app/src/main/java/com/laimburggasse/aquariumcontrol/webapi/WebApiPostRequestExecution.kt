package com.laimburggasse.aquariumcontrol.webapi

import com.laimburggasse.aquariumcontrol.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun webApiPostRequestExecution(
    apiUrl: String,
    mainViewModel: MainViewModel,
    webApiPostRequest: WebApiPostRequest,
    optionalGetParams: Map<String, String> = emptyMap()
): Boolean {
    val user = mainViewModel.getControllerUser()
    val password = mainViewModel.getControllerPassword()

    if (user == null || password == null) {
        return false
    }
    var url =
        "http://" + mainViewModel.getControllerAddress() + apiUrl + "?"

    for ((index, entry) in optionalGetParams.entries.withIndex()) {
        val (key, value) = entry
        url += "$key=$value"
        if (index != optionalGetParams.size - 1) {
            url += "&"
        }
    }

    return withContext(Dispatchers.IO) {
        webApiPostRequest.postData(url, username = user, password).isSuccess
    }
    // @todo: handle the error case
    // @todo: move the toast to a more appropriate place
    //else {
    //    Toast.makeText(
    //        context,
    //        "Currently selected controller does not have " +
    //                "proper configuration of user and password",
    //        Toast.LENGTH_SHORT
    //    ).show()
    //}
}