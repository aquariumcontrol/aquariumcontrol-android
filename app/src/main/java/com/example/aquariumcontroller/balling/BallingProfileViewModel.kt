package com.example.aquariumcontroller.balling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BallingProfileViewModel : ViewModel() {
    private val _ballingNewDosingVolume = MutableLiveData<Double>()
    private val _ballingConfirmedDosingVolume = MutableLiveData<Double>()

    val ballingNewDosingVolume: LiveData<Double> = _ballingNewDosingVolume
    val ballingConfirmedDosingVolume: LiveData<Double> = _ballingConfirmedDosingVolume

    fun onVolumeChange(volume: Double) {
        _ballingNewDosingVolume.value = volume
    }

    fun onConfirmDosing() {
        _ballingConfirmedDosingVolume.value = _ballingNewDosingVolume.value
    }
}