package com.laimburggasse.aquariumcontrol.balling.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.balling.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.BallingRepository
import javax.inject.Singleton

@Singleton
class BallingProfileEditViewModelFactory(
    ballingProfileId: Int,
    ballingRepository: BallingRepository
) : ViewModelProvider.Factory {
    // This class creates the view model BallingProfileEditViewModel
    private val viewModel = BallingProfileEditViewModel(ballingProfileId, ballingRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}