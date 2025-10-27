package com.laimburggasse.aquariumcontrol.controller.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao

class ControllerSelectionViewModelFactory(
    dao: ControllerProfileDao,
    sessionManager: SessionManager
) : ViewModelProvider.Factory {

    // This class creates the view model ControllerSelectionViewModel
    private val viewModel = ControllerSelectionViewModel(dao, sessionManager)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}