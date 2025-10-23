package com.laimburggasse.aquariumcontrol.balling.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.balling.common.BallingProfilesRepository

class BallingProfilesViewModelFactory(
    ballingProfilesRepository: BallingProfilesRepository
) : ViewModelProvider.Factory {

    // This class creates the view model BallingProfileViewModel
    private val viewModel = BallingProfilesViewModel(ballingProfilesRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}