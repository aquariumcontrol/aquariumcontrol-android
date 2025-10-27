package com.laimburggasse.aquariumcontrol.controller.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao

class ControllerEditViewModelFactory(
    dao: ControllerProfileDao,
    sessionManager: SessionManager,
    profileId: Long
) : ViewModelProvider.Factory {

    // This class creates the view model ControllerEditViewModel
    private val viewModel = ControllerEditViewModel(dao, sessionManager, profileId)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}