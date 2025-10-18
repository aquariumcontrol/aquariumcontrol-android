package com.laimburggasse.aquariumcontrol.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class OverviewViewModelFactory (
    overviewRepository: OverviewRepository
) : ViewModelProvider.Factory {
    // This class creates the view model OverviewViewModel
    private val viewModel = OverviewViewModel(overviewRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}