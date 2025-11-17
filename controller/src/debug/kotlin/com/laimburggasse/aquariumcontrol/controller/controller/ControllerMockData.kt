package com.laimburggasse.aquariumcontrol.controller.controller

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState

object ControllerMockData {
    val mockControllerState = ControllerAddEditUiState(
        controllerProfileAddress = "mock.hostname.com",
        controllerProfilePort = 80,
    )

    val mockControllerEditState = ControllerAddEditUiState(
        controllerProfileName = "Controller",
        controllerProfileAddress = "192.168.1.1",
        controllerProfilePort = 80,
        controllerProfileUser = "user",
        controllerProfilePassword = "password",
    )

    val mockControllerProfile1 = ControllerProfile(
        id = 1,
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
    )
    val mockControllerProfile2 = ControllerProfile(
        id = 2,
        name = "Controller 2",
        address = "servername.external.com",
        port = 80,
        user = "userXYZ",
        password = "passwordXYZ",
    )

    val mockControllerProfileList: List<ControllerProfile> = listOf(
        mockControllerProfile1, mockControllerProfile2
    )
}