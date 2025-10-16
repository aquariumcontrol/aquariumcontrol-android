package com.laimburggasse.aquariumcontrol.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScheduleViewModelFactory(
    scheduleRepository: ScheduleRepository
) : ViewModelProvider.Factory {
    private val viewModel = ScheduleViewModel(scheduleRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}