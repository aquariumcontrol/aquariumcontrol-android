package com.laimburggasse.aquariumcontrol.heating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class HeatingStatsViewModelFactory () : ViewModelProvider.Factory {
    // This class creates the view model HeatingStatsViewModel
    private val viewModel = HeatingStatsViewModel()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}