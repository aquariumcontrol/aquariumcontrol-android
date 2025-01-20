package com.example.aquariumcontroller.balling

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
object BallingProfileViewModelFactory : ViewModelProvider.Factory {
    private val viewModel = BallingProfileViewModel()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("BallingProfileViewModelFactory", "create")
        return viewModel as T
    }
}