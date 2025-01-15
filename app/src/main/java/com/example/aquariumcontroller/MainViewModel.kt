package com.example.aquariumcontroller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.controller.ControllerProfile

class MainViewModel: ViewModel() {
    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        selectedController = controllerProfile
    }

    fun isControllerLoaded(): Boolean {
        return selectedController != null
    }

    private var selectedController: ControllerProfile? by mutableStateOf(null)
}