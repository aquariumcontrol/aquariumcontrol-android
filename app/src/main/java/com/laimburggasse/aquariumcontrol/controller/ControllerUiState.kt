package com.laimburggasse.aquariumcontrol.controller
sealed class ControllerUiState {
    object Loading : ControllerUiState()
    data class Success(val controllerProfiles: List<ControllerProfile>) : ControllerUiState()
    data class Error(val errorMessage: String) : ControllerUiState()
}