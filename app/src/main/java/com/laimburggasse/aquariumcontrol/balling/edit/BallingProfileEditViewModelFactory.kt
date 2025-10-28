package com.laimburggasse.aquariumcontrol.balling.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.balling.common.BallingProfilesRepository
import javax.inject.Singleton

@Singleton
class BallingProfileEditViewModelFactory(
    ballingProfileId: Int,
    ballingProfilesRepository: BallingProfilesRepository
) : ViewModelProvider.Factory {
    // This class creates the view model BallingProfileEditViewModel
    private val viewModel = BallingProfileEditViewModel(ballingProfilesRepository, ballingProfileId)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}