package com.laimburggasse.aquariumcontrol.controller.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao

class ControllerAddViewModelFactory(
    dao: ControllerProfileDao,
) : ViewModelProvider.Factory {

    // This class creates the view model ControllerAddViewModel
    private val viewModel = ControllerAddViewModel(dao)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}