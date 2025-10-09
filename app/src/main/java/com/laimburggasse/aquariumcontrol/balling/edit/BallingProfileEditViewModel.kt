package com.laimburggasse.aquariumcontrol.balling.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.balling.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.BallingRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.launch

class BallingProfileEditViewModel(
    ballingProfileId: Int,
    private val repository: BallingRepository,
) : ViewModel() {
    // This class will allow the user to edit the Balling dosing profile.
    private val _ballingNewDosingVolume = MutableLiveData<Double>()
    private val _ballingConfirmedDosingVolume = MutableLiveData<Double>()

    var ballingProfileEditUiState: BallingProfileEditUiState by mutableStateOf(
        value = BallingProfileEditUiState.Loading
    )

    val ballingNewDosingVolume: LiveData<Double> = _ballingNewDosingVolume

    init {
        viewModelScope.launch {
            val result: DataFetchResult<List<BallingProfile>> = repository.fetchBallingProfiles()
            when (result) {
                is DataFetchResult.Success -> {
                    val ballingProfile: BallingProfile? = result.data.find {
                        it.pumpId == ballingProfileId
                    }
                    ballingProfileEditUiState = if (ballingProfile != null) {
                        BallingProfileEditUiState.Success(ballingProfile)
                    } else {
                        BallingProfileEditUiState.NotFound
                    }
                }
                is DataFetchResult.Error -> {
                    // Handle the case where the repository couldn't fetch data
                    ballingProfileEditUiState = BallingProfileEditUiState.Error(result.errorMessage)
                }
            }
        }    }

    fun onVolumeChange(volume: Double) {
        _ballingNewDosingVolume.value = volume
    }

    fun onConfirmDosing() {
        _ballingConfirmedDosingVolume.value = _ballingNewDosingVolume.value
        when (ballingProfileEditUiState) {
            BallingProfileEditUiState.Loading -> {
                // do nothing
            }
            BallingProfileEditUiState.NotFound -> {
                // do nothing
            }
            is BallingProfileEditUiState.Error -> {
                // do nothing
            }
            is BallingProfileEditUiState.Success -> {
                val ballingProfile: BallingProfile = (ballingProfileEditUiState as BallingProfileEditUiState.Success).ballingProfile
                ballingProfile.volume = _ballingNewDosingVolume.value!!
                viewModelScope.launch {
                    repository.updateBallingProfile(ballingProfile)
                }
            }
        }
    }
}