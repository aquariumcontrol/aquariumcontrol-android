package com.laimburggasse.aquariumcontrol.schedule

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockScheduleRepository(
    private val mockScheduleDataFetchResult: DataFetchResult<ScheduleEntries>
): ScheduleRepository(mainViewModel = MockMainViewModel()) {
    private val _scheduleFlow = MutableStateFlow<DataFetchResult<ScheduleEntries>>(
        value = DataFetchResult.Success(data = ScheduleEntries())
    )
    override val scheduleFlow = _scheduleFlow.asStateFlow()

    override suspend fun updateSchedule(
        scheduleEntries: ScheduleEntries
    ) {
        // do nothing
    }

    override suspend fun fetchScheduleSetVals() {
        _scheduleFlow.value = mockScheduleDataFetchResult
    }
}