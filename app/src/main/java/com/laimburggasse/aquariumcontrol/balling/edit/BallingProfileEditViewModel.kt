package com.laimburggasse.aquariumcontrol.balling.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.balling.BallingProfile
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.launch

class BallingProfileEditViewModel(
    private val ballingProfile: BallingProfile,
) : ViewModel() {
    // This class will allow the user to edit the Balling dosing profile.
    private val _ballingNewDosingVolume = MutableLiveData<Double>()
    private val _ballingConfirmedDosingVolume = MutableLiveData<Double>()

    val ballingNewDosingVolume: LiveData<Double> = _ballingNewDosingVolume

    init {
        _ballingNewDosingVolume.value = ballingProfile.volume
    }

    fun onVolumeChange(volume: Double) {
        _ballingNewDosingVolume.value = volume
    }

    fun onConfirmDosing(mainViewModel: MainViewModel) {
        _ballingConfirmedDosingVolume.value = _ballingNewDosingVolume.value
        ballingProfile.volume = _ballingNewDosingVolume.value!!
        viewModelScope.launch {
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.BALLING_POST_SET_VALS,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = mapOf(
                    WebApiParams.PUMP_ID to ballingProfile.pumpId.toString(),
                    WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
                )
            )
        }
    }
}