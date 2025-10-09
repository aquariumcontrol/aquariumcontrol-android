package com.laimburggasse.aquariumcontrol.balling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BallingViewModelFactory(
    ballingRepository: BallingRepository
) : ViewModelProvider.Factory {

    // This class creates the view model BallingProfileViewModel
    private val viewModel = BallingViewModel(ballingRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}
