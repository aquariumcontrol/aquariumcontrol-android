package com.example.aquariumcontroller.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScheduleSetValsViewModelFactory (
    private val setValStartTime: Float,
    private val setValStopTime: Float,
    private val setValLimitationActive: Boolean,
) : ViewModelProvider.Factory {
    // This class creates the view model ScheduleSetValsViewModel
    // Different from the other factory classes, this one shall provide multiple instances.

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = ScheduleSetValsViewModel(
            setValStartTime = setValStartTime,
            setValStopTime = setValStopTime,
            setValLimitationActive = setValLimitationActive
        )
        return viewModel as T
    }
}