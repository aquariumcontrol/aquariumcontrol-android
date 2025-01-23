package com.example.aquariumcontroller.balling.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.balling.BallingProfile

class BallingProfileEditViewModel(
    private val ballingProfile: BallingProfile,
) : ViewModel() {
    // This class will allow the user to edit the Balling dosing profile.
    private val _ballingNewDosingVolume = MutableLiveData<Double>()
    private val _ballingConfirmedDosingVolume = MutableLiveData<Double>()

    val ballingNewDosingVolume: LiveData<Double> = _ballingNewDosingVolume
    val ballingConfirmedDosingVolume: LiveData<Double> = _ballingConfirmedDosingVolume

    init {
        _ballingNewDosingVolume.value = ballingProfile.volume
    }

    fun onVolumeChange(volume: Double) {
        _ballingNewDosingVolume.value = volume
    }

    fun onConfirmDosing() {
        _ballingConfirmedDosingVolume.value = _ballingNewDosingVolume.value
        ballingProfile.volume = _ballingNewDosingVolume.value!!
    }
}