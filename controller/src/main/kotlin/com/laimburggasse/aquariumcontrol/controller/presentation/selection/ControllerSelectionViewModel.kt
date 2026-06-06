/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.controller.presentation.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Controller profile selection screen.
 * Handles the retrieval and manual/automatic selection of the profile
 *
 * @property dao The Data Access Object for database operations on controller profiles.
 * @property sessionManager The manager for global session state and profile selection.
 */
class ControllerSelectionViewModel @Inject constructor(
    private val dao: ControllerProfileDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    /** The list of available controller profiles retrieved from the database. */
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
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
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
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = 0.0f // The countdown starts at 0% progress
    )

    /**
     * Central event handler for all UI interactions on the controller selection screen.
     * Manages input selection and deletion of profiles as well as automatic navigation
     *
     * @param event The specific [ControllerSelectionUiEvent] to process.
     */
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