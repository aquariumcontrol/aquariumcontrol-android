package com.laimburggasse.aquariumcontrol.controller.presentation.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.data.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerSelectionViewModel @Inject constructor(
    private val dao: ControllerProfileDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    // This class represents the view model for the controller screen.
    // Initialize with an empty list or a loading state if preferred
    val controllerProfiles: StateFlow<List<ControllerProfile>> =
        dao.getAllControllerProfiles()
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = emptyList()
            )

    /**
     * A reactive state flow that is `true` only when the conditions for
     * automatically navigating to the default profile are met.
     * The UI can observe this state to trigger side effects.
     */
    val canStartWaitForDefaultNavigation: StateFlow<Boolean> = combine(
        flow = controllerProfiles,
        flow2 = sessionManager.isFirstLoad,
        flow3 = sessionManager.defaultControllerProfileId
    ) { profiles, isFirstLoad, defaultId ->
        profiles.isNotEmpty() && (defaultId != null) && isFirstLoad
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = false
    )

    /**
     * A state flow that emits progress values from 0.0f to 1.0f over 3 seconds.
     * The countdown starts automatically when `canNavigateToDefaultProfile` becomes true.
     * It uses `transformLatest` to ensure that if the condition becomes false and then
     * true again, a new countdown is started, cancelling the old one.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val progressToDefaultNavigation: StateFlow<Float> =
        canStartWaitForDefaultNavigation.transformLatest { canNavigate ->
        if (canNavigate) {
            val countdownDurationMillis = 3000L
            val intervalMillis = 50L // Update ~20 times per second
            var elapsedMillis = 0L

            while (elapsedMillis < countdownDurationMillis) {
                // Emit the current progress as a float between 0.0 and 1.0
                emit(value = elapsedMillis.toFloat() / countdownDurationMillis)
                delay(timeMillis = intervalMillis)
                elapsedMillis += intervalMillis
            }
            // Ensure it emits the final 1.0f value
            emit(value = 1.0f)
        } else {
            // If the condition is not met, emit 0.0f to reset any progress indicator.
            emit(value = 0.0f)
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = 0.0f // The countdown starts at 0% progress
    )

    fun onEvent(event: ControllerSelectionUiEvent) {
        when (event) {
            is ControllerSelectionUiEvent.DeleteControllerSelectionProfile -> {
                viewModelScope.launch {
                    dao.deleteControllerProfile(controllerProfile = event.controllerProfile)
                }
            }
            is ControllerSelectionUiEvent.SelectControllerProfile -> {
                sessionManager.updateSelectedControllerProfile(controllerProfile = event.controllerProfile)
            }
            ControllerSelectionUiEvent.AutoNavigationFinished -> {
                // do nothing
            }
        }
    }
}