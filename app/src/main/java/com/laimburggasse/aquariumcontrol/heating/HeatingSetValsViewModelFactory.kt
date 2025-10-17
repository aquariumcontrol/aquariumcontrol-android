package com.laimburggasse.aquariumcontrol.heating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class HeatingSetValsViewModelFactory (
    heatingSetValsRepository: HeatingSetValsRepository,
) : ViewModelProvider.Factory {
    // This class creates the view model HeatingSetValsViewModel
    private val viewModel = HeatingSetValsViewModel(heatingSetValsRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}