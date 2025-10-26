package com.laimburggasse.aquariumcontrol

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.laimburggasse.aquariumcontrol.MainActivity.Companion.KEY_DEFAULT_PROFILE_ID
import com.laimburggasse.aquariumcontrol.MainActivity.Companion.NO_DEFAULT_ID
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class SessionManager(
    private val sharedPreferences: SharedPreferences? = null
): ViewModel() {
    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)
    val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile.asStateFlow()

    private var previousControllerProfileId: Long? = null

    private val _defaultControllerProfileId = MutableStateFlow(
        value = sharedPreferences?.getLong(KEY_DEFAULT_PROFILE_ID, NO_DEFAULT_ID).let {
            if (it == NO_DEFAULT_ID) null else it
        }
    )
    val defaultControllerProfileId: StateFlow<Long?> = _defaultControllerProfileId.asStateFlow()

    private val _isFirstLoad = MutableStateFlow(value = true)
    val isFirstLoad: StateFlow<Boolean> = _isFirstLoad.asStateFlow()

    fun setSelectedControllerProfile(controllerProfile: ControllerProfile) {
        _selectedControllerProfile.value = controllerProfile
    }

    fun saveDefaultControllerProfileId(id: Long) {
        sharedPreferences?.edit {
            putLong(KEY_DEFAULT_PROFILE_ID, id)
        } // .apply() writes asynchronously in the background.
        _defaultControllerProfileId.value = id
    }

    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        _selectedControllerProfile.value = controllerProfile
    }

    fun isControllerLoaded(): Boolean {
        return selectedControllerProfile.value != null
    }

    open fun getControllerAddress(): String? {
        return selectedControllerProfile.value?.address
    }

    open fun getControllerUser(): String? {
        return selectedControllerProfile.value?.user
    }

    fun getControllerPassword(): String? {
        return selectedControllerProfile.value?.password
    }

    open fun getControllerPort(): Int? {
        return selectedControllerProfile.value?.port
    }

    open fun getControllerName(): String? {
        return selectedControllerProfile.value?.name
    }

    fun setFirstLoad() {
        _isFirstLoad.value = false
    }

    fun unsetDefaultControllerId() {
        if (previousControllerProfileId != null) {
            _defaultControllerProfileId.value = previousControllerProfileId!!
        }
    }
}