package com.laimburggasse.aquariumcontroller.ventilation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class VentilationSetValsViewModelFactory (
    ventilationSetVals: VentilationSetVals,
) : ViewModelProvider.Factory {
    // This class creates the view model VentilationSetValsViewModel
    private val viewModel = VentilationSetValsViewModel(ventilationSetVals)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}