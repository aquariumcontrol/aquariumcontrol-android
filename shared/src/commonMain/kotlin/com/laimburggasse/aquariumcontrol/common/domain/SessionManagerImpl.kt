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

package com.laimburggasse.aquariumcontrol.common.domain

import com.laimburggasse.aquariumcontrol.common.util.DispatcherIO

import me.tatarka.inject.annotations.Inject

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of [SessionManager] responsible for managing the active controller selection
 * and tracking application-wide session state.
 *
 * @property dataStore The DataStore instance for persisting settings.
 * @property dao The DAO for fetching controller profile details.
 * @property externalScope A coroutine scope for managing background tasks tied to the session lifetime.
 */
@Inject
open class SessionManagerImpl(
    private val dataStore: DataStore<Preferences>,
    private val dao: ControllerProfileDao,
    private val externalScope: CoroutineScope
): SessionManager {
    /** Internal mutable flow for the currently selected controller profile. */
    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)

    /** Public reactive flow emitting the active [ControllerProfile]. */
    override val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile.asStateFlow()

    /** Reactive state indicating if any controller is currently selected. */
    override val isControllerLoaded: StateFlow<Boolean> = selectedControllerProfile.map { it != null }
        .stateIn(
            started = SharingStarted.Eagerly,
            initialValue = false,
            scope = externalScope
        )

    /** Stores the previously selected ID for undo/revert operations. */
    private var previousControllerProfileId: Long? = null

    /** DataStore key for the default profile ID. */
    private val defaultProfileIdKey = longPreferencesKey(GlobalConstants.KEY_DEFAULT_PROFILE_ID)

    /** Internal mutable flow for the default profile ID. */
    private val _defaultControllerProfileId = MutableStateFlow<Long?>(value = null)

    /** Reactive flow emitting the ID of the default controller profile. */
    override val defaultControllerProfileId: StateFlow<Long?> = _defaultControllerProfileId.asStateFlow()

    /** Internal mutable state for the first-load auto-navigation flag. */
    private val _isFirstLoad = MutableStateFlow(value = true)

    /** Flag indicating if the app is currently in its first-load sequence. */
    override val isFirstLoad: StateFlow<Boolean> = _isFirstLoad.asStateFlow()

    /** Internal mutable flow for the most recent server-reported timestamp. */
    private val _serverTimestamp = MutableStateFlow<Long?>(value = null)

    /** Reactive flow emitting the latest epoch milliseconds from the controller. */
    override val serverTimestamp: StateFlow<Long?> = _serverTimestamp.asStateFlow()

    init {
        externalScope.launch {
            val prefs = dataStore.data.first()
            val defaultId = prefs[defaultProfileIdKey] ?: GlobalConstants.NO_DEFAULT_ID

            if (defaultId != GlobalConstants.NO_DEFAULT_ID) {
                _defaultControllerProfileId.value = defaultId
                // Automatically select the default profile on startup
                selectControllerById(defaultId)
            }
        }
    }

    /**
     * Fetches a profile by ID and sets it as the active controller.
     *
     * @param id The unique ID of the profile to select.
     * @return True if selection was successful, false if the profile was not found.
     */
    override suspend fun selectControllerById(id: Long): Boolean {
        previousControllerProfileId = _defaultControllerProfileId.value
        return withContext(context = DispatcherIO) {
            val profile: ControllerProfile? = dao.getControllerProfileById(id)
            if (profile != null) {
                _selectedControllerProfile.value = profile
                true
            } else {
                false
            }
        }
    }

    /**
     * Persists the given ID as the default profile to DataStore.
     *
     * @param id The profile ID to set as default.
     */
    override fun saveDefaultControllerProfileId(id: Long) {
        externalScope.launch {
            dataStore.edit { settings ->
                settings[defaultProfileIdKey] = id
            }
        }
        _defaultControllerProfileId.value = id
    }

    /**
     * Manually updates the currently selected profile state.
     *
     * @param controllerProfile The updated profile entity.
     */
    override fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        _selectedControllerProfile.value = controllerProfile
    }

    /**
     * Updates the global server timestamp flow.
     *
     * @param timestamp The new timestamp from the server.
     */
    override fun updateServerTimestamp(timestamp: Long?) {
        _serverTimestamp.value = timestamp
    }

    /**
     * Utility to get the current controller's address.
     * @return The address string, or null if no controller is selected.
     */
    override fun getControllerAddress(): String? {
        return selectedControllerProfile.value?.address
    }

    /**
     * Utility to get the current controller's username.
     * @return The username, or null if no controller is selected.
     */
    override fun getControllerUser(): String? {
        return selectedControllerProfile.value?.user
    }

    /**
     * Utility to get the current controller's password.
     * @return The password, or null if no controller is selected.
     */
    override fun getControllerPassword(): String? {
        return selectedControllerProfile.value?.password
    }

    /**
     * Utility to get the current controller's port.
     * @return The port number, or null if no controller is selected.
     */
    override fun getControllerPort(): Int? {
        return selectedControllerProfile.value?.port
    }

    /**
     * Disables the automatic navigation to the default profile after initial load.
     */
    override fun suppressAutoNavigation() {
        _isFirstLoad.value = false
    }

    /**
     * Reverts the default profile ID selection to its previous state.
     */
    override fun unsetDefaultControllerId() {
        if (previousControllerProfileId != null) {
            _defaultControllerProfileId.value = previousControllerProfileId!!
        }
    }
}