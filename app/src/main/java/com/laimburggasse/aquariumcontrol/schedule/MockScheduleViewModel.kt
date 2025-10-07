package com.laimburggasse.aquariumcontrol.schedule

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockScheduleViewModel(override val scheduleUiState: ScheduleUiState) : ScheduleViewModelInterface {
    override fun updateSchedule(
        mainViewModel: MainViewModel,
        scheduleEntries: ScheduleEntries
    ) {
        // do nothing
    }

    override fun loadData(
        mainViewModel: MainViewModel,
        ballingScheduleEntryViewModel: ScheduleEntryViewModel,
        refillScheduleEntryViewModel: ScheduleEntryViewModel,
        ventilationScheduleEntryViewModel: ScheduleEntryViewModel,
        heatingScheduleEntryViewModel: ScheduleEntryViewModel
    ) {
        // do nothing
    }

}