package com.laimburggasse.aquariumcontrol.refill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RefillViewModel(private val refillRepository: RefillRepository): ViewModel() {
    fun sendStartRequest() {
        viewModelScope.launch {
            refillRepository.sendStartRequest()
        }
    }

    fun sendStopRequest() {
        viewModelScope.launch {
            refillRepository.sendStopRequest()
        }
    }

    fun sendResetRequest() {
        viewModelScope.launch {
            refillRepository.sendResetRequest()
        }
    }

}