package com.example.aquariumcontroller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    internal var controllerIsLoaded by mutableStateOf(false)
}