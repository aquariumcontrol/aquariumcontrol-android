package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class BallingRepository(private val mainViewModel: MainViewModel) {
    private var cachedProfiles: List<BallingProfile> = emptyList()

    open suspend fun fetchBallingProfiles(): DataFetchResult<List<BallingProfile>> {
        val url: String = "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_SET_VALS
        val reader = BallingProfileReader(url)
        return withContext(context = Dispatchers.IO) {
            reader.readBallingProfiles()
        }
    }

    open fun getBallingProfileById(id: Int): BallingProfile? {
        // get Balling profile from local cache
        return cachedProfiles.find { it.pumpId == id }
    }

    suspend fun updateBallingProfile(ballingProfile: BallingProfile) {
        // update the local cache is not required, because BallingProfileEditViewModel holds reference
        webApiPostRequestExecution(
            mainViewModel = mainViewModel,
            apiUrl = WebApiUrls.BALLING_POST_SET_VALS,
            webApiPostRequest = WebApiPostRequest(),
            optionalGetParams = mapOf(
                WebApiParams.PUMP_ID to ballingProfile.pumpId.toString(),
                WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
            )
        )

    }
}