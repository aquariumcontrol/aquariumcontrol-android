/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.common.testing

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockSessionManagerImpl(
    private var controllerProfiles: List<ControllerProfile>
): SessionManager {
    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)
    override val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile

    private val _isControllerLoaded = MutableStateFlow<Boolean>(value = false)
    override val isControllerLoaded: StateFlow<Boolean> = _isControllerLoaded

    private val _defaultControllerProfileId = MutableStateFlow<Long?>(value = null)
    override val defaultControllerProfileId: StateFlow<Long?> = _defaultControllerProfileId

    private val _isFirstLoad = MutableStateFlow(value = true)
    override val isFirstLoad: StateFlow<Boolean> = _isFirstLoad

    private val _serverTimestamp = MutableStateFlow<String?>(value = null)
    override val serverTimestamp: StateFlow<String?> = _serverTimestamp

    private var previousControllerProfileId: Long? = null

    fun setMockControllerList(list: List<ControllerProfile>) {
        controllerProfiles = list
    }

    override suspend fun selectControllerById(id: Long): Boolean {
        previousControllerProfileId = _defaultControllerProfileId.value
        val profile: ControllerProfile? = controllerProfiles.find { it.id == id }
        if (profile != null) {
            _selectedControllerProfile.value = profile
            return true
        } else {
            return false
        }
    }

    override fun saveDefaultControllerProfileId(id: Long) {
        _defaultControllerProfileId.value = id
    }

    override fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        _selectedControllerProfile.value = controllerProfile
    }

    override fun updateServerTimestamp(timestamp: String?) {
        _serverTimestamp.value = timestamp
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