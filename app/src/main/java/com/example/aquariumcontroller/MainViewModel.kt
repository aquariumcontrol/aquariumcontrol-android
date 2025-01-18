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

    fun getControllerAddress(): String? {
        return selectedController?.address
    }

    fun getControllerUser(): String? {
        return selectedController?.user
    }

    fun getControllerPassword(): String? {
        return selectedController?.password
    }

    fun getControllerPort(): Int? {
        return selectedController?.port
    }

    fun getControllerName(): String? {
        return selectedController?.name
    }
    private var selectedController: ControllerProfile? by mutableStateOf(null)
}