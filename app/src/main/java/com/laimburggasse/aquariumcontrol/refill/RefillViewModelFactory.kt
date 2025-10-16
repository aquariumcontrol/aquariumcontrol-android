package com.laimburggasse.aquariumcontrol.refill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class RefillViewModelFactory (
    refillRepository: RefillRepository
) : ViewModelProvider.Factory {
    // This class creates the view model RefillViewModel
    private val viewModel = RefillViewModel(refillRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}