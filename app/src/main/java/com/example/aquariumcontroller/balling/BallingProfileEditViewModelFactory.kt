package com.example.aquariumcontroller.balling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aquariumcontroller.MainViewModel
import javax.inject.Singleton

@Singleton
class BallingProfileEditViewModelFactory(
    ballingProfile: BallingProfile,
) : ViewModelProvider.Factory {
    private val viewModel = BallingProfileEditViewModel(ballingProfile)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}