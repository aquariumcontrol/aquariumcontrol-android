package com.laimburggasse.aquariumcontrol.schedule

import com.laimburggasse.aquariumcontrol.MainViewModel

interface ScheduleViewModelInterface {
    val scheduleUiState: ScheduleUiState

    fun updateSchedule(
        mainViewModel: MainViewModel,
        scheduleEntries: ScheduleEntries
    )

    fun loadData(
        mainViewModel: MainViewModel,
        ballingScheduleEntryViewModel: ScheduleEntryViewModel,
        refillScheduleEntryViewModel: ScheduleEntryViewModel,
        ventilationScheduleEntryViewModel: ScheduleEntryViewModel,
        heatingScheduleEntryViewModel: ScheduleEntryViewModel
    )
}