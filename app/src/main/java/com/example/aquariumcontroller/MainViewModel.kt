package com.example.aquariumcontroller

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.balling.BallingProfile
import com.example.aquariumcontroller.controller.ControllerProfile

class MainViewModel: ViewModel() {
    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        selectedController = controllerProfile
    }

    fun updateSelectedBallingProfile(ballingProfile: BallingProfile) {
        selectedBallingProfile = ballingProfile
    }

    fun getSelectedBallingProfileLabel(): String? {
        return selectedBallingProfile?.label
    }

    fun getSelectedBallingProfileVolume(): Double? {
        return selectedBallingProfile?.volume
    }

    fun getSelectedBallingProfilePumpId(): Int? {
        return selectedBallingProfile?.pumpId
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

    fun updateSelectedBallingProfileVolume(sliderPositionDosingVolume: Float) {
        if (selectedBallingProfile != null) {
            selectedBallingProfile!!.volume = sliderPositionDosingVolume.toDouble()
            Log.d(this::class.simpleName, "updateSelectedBallingProfileVolume: " + selectedBallingProfile!!.volume)
        }
    }

    private var selectedController: ControllerProfile? by mutableStateOf(null)

    private var selectedBallingProfile: BallingProfile? by mutableStateOf(null)
}