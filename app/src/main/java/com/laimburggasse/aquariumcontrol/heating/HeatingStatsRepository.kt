package com.laimburggasse.aquariumcontrol.heating

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class HeatingStatsRepository(
    private val mainViewModel: MainViewModel,
    externalScope: CoroutineScope? = null
) {
    private val _heatingStatsFlow = MutableStateFlow<DataFetchResult<List<HeatingStatsEntry>>>(
        value = DataFetchResult.Success(data = emptyList())
    )
    open val heatingStatsFlow = _heatingStatsFlow.asStateFlow()

    open suspend fun fetchHeatingStats(){
        return withContext(context = Dispatchers.IO) {
            val url: String =
                "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_STATS
            val heatingStatsReader = HeatingStatsReader(url)
            withContext(context = Dispatchers.IO) {
                val result: DataFetchResult<List<HeatingStatsStringEntry>> =
                    heatingStatsReader.readStatsEntry()
                val transformedResult: DataFetchResult<List<HeatingStatsEntry>> = when (result) {
                    is DataFetchResult.Success -> {
                        val heatingStringsStats: List<HeatingStatsStringEntry> = result.data
                        val heatingStats: List<HeatingStatsEntry> = heatingStringsStats.map {
                            it.transformToHeatingStatsEntry()
                        }
                        DataFetchResult.Success(data = heatingStats)
                    }
                    is DataFetchResult.Error -> {
                        result
                    }
                }
                _heatingStatsFlow.value = transformedResult
            }
        }
    }

    init {
        externalScope?.let {
            externalScope.launch {
                fetchHeatingStats()
            }
        }
    }
}