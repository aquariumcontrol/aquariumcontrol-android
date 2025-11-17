package com.laimburggasse.aquariumcontrol.overview.testing

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewUiState

object OverviewMockData {
    val mockOverviewData = OverviewData(
        timestamp = "2023-05-01 12:00:00",
        temperatureWater = 25.0,
        temperatureWaterFiltered = 25.5,
        pH = 7.0,
        pHFiltered = 7.5,
        conductivity = 1000.0,
        conductivityFiltered = 1005.0,
        tankLevelSwitchPosition = "HIGH",
        surfaceVentilation = "ON",
        temperatureAmbient = 22.0,
        humidityAmbient = 60.0,
        heater = "OFF"
    )
    val mockControllerProfile = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
    )
    val mockOverviewUiStateSuccess = OverviewUiState.Success(mockOverviewData)
    val mockOverviewUiStateError = OverviewUiState.Error(errorMessage = "Mock Error loading data")
}