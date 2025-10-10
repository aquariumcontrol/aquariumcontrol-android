package com.laimburggasse.aquariumcontrol.refill.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RefillHistoryViewModelFactory(
    refillHistoryRepository: RefillHistoryRepository
) : ViewModelProvider.Factory {

    // This class create the view model RefillHistoryViewModel
    private val viewModel = RefillHistoryViewModel(refillHistoryRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}