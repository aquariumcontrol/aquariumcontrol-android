package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.MainViewModel

interface BallingViewModelInterface {
    var ballingUiState: BallingUiState
    var isRefreshing: Boolean
    fun loadData()

    fun getBallingProfile(id: Int): BallingProfile? {
        // function is common for implementation and mock version
        when (ballingUiState) {
            is BallingUiState.Success -> {
                val ballingProfiles: List<BallingProfile> = (ballingUiState as BallingUiState.Success).ballingProfiles
                val ballingProfile = ballingProfiles.find { it.pumpId == id }
                return ballingProfile
            }
            else -> {
                return null
            }
        }
    }
}