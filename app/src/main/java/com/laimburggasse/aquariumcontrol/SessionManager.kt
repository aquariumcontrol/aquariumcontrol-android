package com.laimburggasse.aquariumcontrol

import android.content.SharedPreferences
import androidx.core.content.edit
import com.laimburggasse.aquariumcontrol.MainActivity.Companion.KEY_DEFAULT_PROFILE_ID
import com.laimburggasse.aquariumcontrol.MainActivity.Companion.NO_DEFAULT_ID
import com.laimburggasse.aquariumcontrol.controller.data.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
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

open class SessionManager(
    private val sharedPreferences: SharedPreferences? = null,
    private val dao: ControllerProfileDao,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)
    val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile.asStateFlow()
    val isControllerLoaded: StateFlow<Boolean> = selectedControllerProfile.map { it != null }
        .stateIn(
            started = SharingStarted.Eagerly,
            initialValue = false,
            scope = externalScope
        )
    private var previousControllerProfileId: Long? = null

    // Initialize with null. The value will be loaded asynchronously.
    private val _defaultControllerProfileId = MutableStateFlow<Long?>(value = null)
    val defaultControllerProfileId: StateFlow<Long?> = _defaultControllerProfileId.asStateFlow()

    private val _isFirstLoad = MutableStateFlow(value = true)
    val isFirstLoad: StateFlow<Boolean> = _isFirstLoad.asStateFlow()

    init {
        externalScope.launch {
            val defaultId = withContext(context = Dispatchers.IO) { // Use Dispatchers.IO for file access
                sharedPreferences?.getLong(KEY_DEFAULT_PROFILE_ID, NO_DEFAULT_ID)
            }
            if (defaultId != NO_DEFAULT_ID) {
                // Emit the loaded value. This will trigger the 'combine' in the ViewModel.
                _defaultControllerProfileId.value = defaultId
            }
        }
    }

    suspend fun selectControllerById(id: Long): Boolean {
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
    fun saveDefaultControllerProfileId(id: Long) {
        sharedPreferences?.edit {
            putLong(KEY_DEFAULT_PROFILE_ID, id)
        } // .apply() writes asynchronously in the background.
        _defaultControllerProfileId.value = id
    }

    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        _selectedControllerProfile.value = controllerProfile
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

    fun suppressAutoNavigation() {
        _isFirstLoad.value = false
    }

    fun unsetDefaultControllerId() {
        if (previousControllerProfileId != null) {
            _defaultControllerProfileId.value = previousControllerProfileId!!
        }
    }
}