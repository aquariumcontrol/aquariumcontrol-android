package com.laimburggasse.aquariumcontrol.balling.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BallingHistoryViewModelFactory(
    ballingHistoryRepository: BallingHistoryRepository
) : ViewModelProvider.Factory {

    // This class creates the view model BallingHistoryViewModel
    private val viewModel = BallingHistoryViewModel(ballingHistoryRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}