package com.laimburggasse.aquariumcontrol.refill.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.refill.data.RefillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefillViewModel @Inject constructor(private val refillRepository: RefillRepository): ViewModel() {
    private val _events = MutableSharedFlow<RefillScreenEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            refillRepository.errorEvents.collect { errorMessage ->
                _events.emit(value = RefillScreenEvent.ShowToast(errorMessage))
            }
        }
    }
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

    fun onRefillEvent(event: RefillUiEvent) {
        when (event) {
            RefillUiEvent.ResetErrors -> {
                sendResetRequest()
            }
            RefillUiEvent.StartControl -> {
                sendStartRequest()
            }
            RefillUiEvent.StopControl -> {
                sendStopRequest()
            }
        }
    }
}