package com.laimburggasse.aquariumcontrol.overview

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class OverviewRepository(private val sessionManager: SessionManager) {
    private var _overviewData = MutableStateFlow<DataFetchResult<OverviewData>>(
        value = DataFetchResult.Success(data = OverviewData())
    )
    open val overviewData = _overviewData.asStateFlow()

    open suspend fun fetchOverviewData() {
        withContext(context = Dispatchers.IO) {
            val url: String =
                "http://" + sessionManager.getControllerAddress() + WebApiUrls.OVERVIEW_GET_DATA
            val reader = OverviewDataReader(url)
            _overviewData.value = reader.readOverviewData()
        }
    }
}