package com.example.aquariumcontroller.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class ScheduleSetValsViewModelFactory (
    scheduleSetVals: ScheduleSetVals,
) : ViewModelProvider.Factory {
    // This class creates the view model ScheduleSetValsViewModel
    private val viewModel = ScheduleSetValsViewModel(scheduleSetVals)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}