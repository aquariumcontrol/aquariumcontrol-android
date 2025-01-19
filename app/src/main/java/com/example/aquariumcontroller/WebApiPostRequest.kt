package com.example.aquariumcontroller

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun webApiPostRequest(
    apiUrl: String,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    postRequest: PostRequest,
    context: Context,
    optionalGetParams: Map<String, String> = emptyMap()
) {
    val user = mainViewModel.getControllerUser()
    val password = mainViewModel.getControllerPassword()

    if (user != null && password != null) {
        var url =
            "http://" + mainViewModel.getControllerAddress() + apiUrl + "?"

        for ((key, value) in optionalGetParams) {
            url += "$key=$value"
            if (optionalGetParams[key] != optionalGetParams.values.last()) {
                url += "&"
            }
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                postRequest.postData(url, user, password)
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