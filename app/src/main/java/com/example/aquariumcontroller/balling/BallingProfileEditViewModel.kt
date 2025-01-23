package com.example.aquariumcontroller.balling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.ApiParams
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.webApiPostRequest

class BallingProfileEditViewModel(
    private val ballingProfile: BallingProfile,
) : ViewModel() {
    private val _ballingNewDosingVolume = MutableLiveData<Double>()
    private val _ballingConfirmedDosingVolume = MutableLiveData<Double>()

    val ballingNewDosingVolume: LiveData<Double> = _ballingNewDosingVolume
    val ballingConfirmedDosingVolume: LiveData<Double> = _ballingConfirmedDosingVolume

    fun onVolumeChange(volume: Double) {
        _ballingNewDosingVolume.value = volume
    }

    fun onConfirmDosing() {
        _ballingConfirmedDosingVolume.value = _ballingNewDosingVolume.value
        ballingProfile.volume = _ballingNewDosingVolume.value!!
    }
}