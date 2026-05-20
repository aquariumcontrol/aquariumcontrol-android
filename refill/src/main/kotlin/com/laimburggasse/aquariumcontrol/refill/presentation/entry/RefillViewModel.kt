/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.refill.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class RefillViewModel @Inject constructor(private val refillRepository: RefillRepository): ViewModel() {
    private val _events = MutableSharedFlow<RefillScreenEvent>()
    val events = _events.asSharedFlow()

    val refillStateUiState: StateFlow<RefillStateUiState> = refillRepository.refillStateFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> RefillStateUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    RefillStateUiState.Success(refillState = result.data)
                }
                DataFetchResult.Loading -> RefillStateUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RefillStateUiState.Loading
        )
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
            RefillUiEvent.Refresh -> {
                viewModelScope.launch {
                    refillRepository.triggerRefresh()
                }
            }
        }
    }
}