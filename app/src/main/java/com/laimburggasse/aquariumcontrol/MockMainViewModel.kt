package com.laimburggasse.aquariumcontrol

import com.laimburggasse.aquariumcontrol.controller.ControllerProfile

class MockMainViewModel(private val controller: ControllerProfile? = null) : MainViewModel() {
    override fun getControllerName(): String? = controller?.name
    override fun getControllerAddress(): String? = controller?.address
    override fun getControllerUser(): String? = controller?.user
    override fun getControllerPort(): Int? = controller?.port
}