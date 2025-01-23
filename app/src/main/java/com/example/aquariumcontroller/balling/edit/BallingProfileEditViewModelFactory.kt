package com.example.aquariumcontroller.balling.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aquariumcontroller.balling.BallingProfile
import javax.inject.Singleton

@Singleton
class BallingProfileEditViewModelFactory(
    ballingProfile: BallingProfile,
) : ViewModelProvider.Factory {
    // This class creates the view model BallingProfileEditViewModel
    private val viewModel = BallingProfileEditViewModel(ballingProfile)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}