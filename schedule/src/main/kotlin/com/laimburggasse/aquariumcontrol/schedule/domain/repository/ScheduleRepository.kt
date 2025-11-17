package com.laimburggasse.aquariumcontrol.schedule.domain.repository

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries
import kotlinx.coroutines.flow.StateFlow

interface ScheduleRepository {
    val scheduleFlow: StateFlow<DataFetchResult<ScheduleEntries>>
    fun updateLocalSchedule(newScheduleEntries: ScheduleEntries)
    suspend fun updateSchedule()
    fun reset()
    suspend fun triggerRefresh()
}