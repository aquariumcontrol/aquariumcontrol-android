package com.laimburggasse.aquariumcontrol.balling

import android.util.Log
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

open class BallingProfilesRepository(private val mainViewModel: MainViewModel) {
    private val _profilesFlow = MutableStateFlow<DataFetchResult<List<BallingProfile>>>(
        value = DataFetchResult.Success(data = emptyList())
    )
    open val profilesFlow = _profilesFlow.asStateFlow()

    open suspend fun fetchBallingProfiles() {
        val url: String = "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_SET_VALS
        val reader = BallingProfileReader(url)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<BallingProfile>> = reader.readBallingProfiles()
            _profilesFlow.value = result
        }
    }

    open fun getBallingProfileById(id: Int): BallingProfile? {
        val currentResult: DataFetchResult<List<BallingProfile>> = profilesFlow.value

        val successResult: DataFetchResult.Success<List<BallingProfile>>? =
            currentResult as? DataFetchResult.Success

        return successResult?.data?.find { it.pumpId == id }
    }

    suspend fun updateBallingProfile(ballingProfile: BallingProfile) {
        // update the local cache is not required, because BallingProfileEditViewModel holds reference
        Log.d("BallingRepository", "Updating balling profile: $ballingProfile")
        val requestSuccess = webApiPostRequestExecution(
            mainViewModel = mainViewModel,
            apiUrl = WebApiUrls.BALLING_POST_SET_VALS,
            webApiPostRequest = WebApiPostRequest(),
            optionalGetParams = mapOf(
                WebApiParams.PUMP_ID to ballingProfile.pumpId.toString(),
                WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
            )
        )


        // Check if the web request was successful before updating the cache
        if (requestSuccess) {
            Log.d("BallingRepository", "Server update successful. Updating local cache.")
            // Find the index of the old profile in the cache
            _profilesFlow.update { currentDataResult ->
                if (currentDataResult is DataFetchResult.Success) {
                    // Create a new list with the updated item
                    val updatedList = currentDataResult.data.map { profileInList ->
                        if (profileInList.pumpId == ballingProfile.pumpId) {
                            ballingProfile // Replace with the updated profile
                        } else {
                            profileInList // Keep the existing one
                        }
                    }
                    DataFetchResult.Success(updatedList) // Emit a new Success state with the new list
                } else {
                    currentDataResult // If not in a success state, do not change anything
                }
            }
        } else {
            // Log the error if the server update failed
            Log.e("BallingRepository", "Server update failed: Local cache not updated.")
        }
    }
}