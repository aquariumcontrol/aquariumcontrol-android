package com.laimburggasse.aquariumcontrol

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.laimburggasse.aquariumcontrol.controller.ControllerProfile

open class MainViewModel: ViewModel() {
    private var selectedController: ControllerProfile? by mutableStateOf(null)
    private var defaultControllerProfileId: Long = 0
    private var firstLoad: Boolean = true

    fun getDefaultControllerProfileId(): Long {
        return defaultControllerProfileId
    }

    fun setDefaultControllerProfileId(id: Long) {
        this.defaultControllerProfileId = id
    }

    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        selectedController = controllerProfile
    }

    fun isControllerLoaded(): Boolean {
        return selectedController != null
    }

    open fun getControllerAddress(): String? {
        return selectedController?.address
    }

    open fun getControllerUser(): String? {
        return selectedController?.user
    }

    fun getControllerPassword(): String? {
        return selectedController?.password
    }

    open fun getControllerPort(): Int? {
        return selectedController?.port
    }

    open fun getControllerName(): String? {
        return selectedController?.name
    }

    fun isFirstLoad(): Boolean {
        return firstLoad
    }

    fun setFirstLoad() {
        firstLoad = false
    }
}