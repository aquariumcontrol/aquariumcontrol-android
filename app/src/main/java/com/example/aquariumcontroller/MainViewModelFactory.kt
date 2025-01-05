package com.example.aquariumcontroller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
object MainViewModelFactory : ViewModelProvider.Factory {
    private val viewModel = MainViewModel()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("MainViewModelFactory", "create")
        return viewModel as T
    }
}