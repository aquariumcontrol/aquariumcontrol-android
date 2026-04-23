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

import android.content.SharedPreferences
import androidx.core.content.edit
import com.laimburggasse.aquariumcontrol.common.data.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class SessionManagerImpl(
    private val sharedPreferences: SharedPreferences? = null,
    private val dao: ControllerProfileDao,
    @param:ApplicationScope private val externalScope: CoroutineScope
): SessionManager {
    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)
    override val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile.asStateFlow()
    override val isControllerLoaded: StateFlow<Boolean> = selectedControllerProfile.map { it != null }
        .stateIn(
            started = SharingStarted.Eagerly,
            initialValue = false,
            scope = externalScope
        )
    private var previousControllerProfileId: Long? = null

    // Initialize with null. The value will be loaded asynchronously.
    private val _defaultControllerProfileId = MutableStateFlow<Long?>(value = null)
    override val defaultControllerProfileId: StateFlow<Long?> = _defaultControllerProfileId.asStateFlow()

    private val _isFirstLoad = MutableStateFlow(value = true)
    override val isFirstLoad: StateFlow<Boolean> = _isFirstLoad.asStateFlow()

    init {
        externalScope.launch {
            val defaultId: Long? =
                withContext(context = Dispatchers.IO) { // Use Dispatchers.IO for file access
                    sharedPreferences?.getLong(
                        GlobalConstants.KEY_DEFAULT_PROFILE_ID,
                        GlobalConstants.NO_DEFAULT_ID
                    )
                }
            if (defaultId != GlobalConstants.NO_DEFAULT_ID) {
                // Emit the loaded value. This will trigger the 'combine' in the ViewModel.
                _defaultControllerProfileId.value = defaultId
            }
        }
    }

    override suspend fun selectControllerById(id: Long): Boolean {
        previousControllerProfileId = _defaultControllerProfileId.value
        return withContext(context = Dispatchers.IO) {
            val profile: ControllerProfile? = dao.getControllerProfileById(id)
            if (profile != null) {
                _selectedControllerProfile.value = profile
                true
            } else {
                false
            }
        }
    }

    override fun saveDefaultControllerProfileId(id: Long) {
        sharedPreferences?.edit {
            putLong(GlobalConstants.KEY_DEFAULT_PROFILE_ID, id)
        } // .apply() writes asynchronously in the background.
        _defaultControllerProfileId.value = id
    }

    override fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        _selectedControllerProfile.value = controllerProfile
    }

    override fun getControllerAddress(): String? {
        return selectedControllerProfile.value?.address
    }

    override fun getControllerUser(): String? {
        return selectedControllerProfile.value?.user
    }

    override fun getControllerPassword(): String? {
        return selectedControllerProfile.value?.password
    }

    override fun getControllerPort(): Int? {
        return selectedControllerProfile.value?.port
    }

    override fun suppressAutoNavigation() {
        _isFirstLoad.value = false
    }

    override fun unsetDefaultControllerId() {
        if (previousControllerProfileId != null) {
            _defaultControllerProfileId.value = previousControllerProfileId!!
        }
    }
}