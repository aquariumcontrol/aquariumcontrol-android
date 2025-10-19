package com.laimburggasse.aquariumcontrol.timedata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class TimeDataViewModelFactory (
    timeDataRepository: TimeDataRepository
) : ViewModelProvider.Factory {
    // This class creates the view model TimeDataViewModel
    private val viewModel = TimeDataViewModel(timeDataRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}