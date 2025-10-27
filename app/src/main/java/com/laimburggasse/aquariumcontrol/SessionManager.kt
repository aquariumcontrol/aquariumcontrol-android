package com.laimburggasse.aquariumcontrol

import android.content.SharedPreferences
import androidx.core.content.edit
import com.laimburggasse.aquariumcontrol.MainActivity.Companion.KEY_DEFAULT_PROFILE_ID
import com.laimburggasse.aquariumcontrol.MainActivity.Companion.NO_DEFAULT_ID
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

open class SessionManager(
    private val sharedPreferences: SharedPreferences? = null,
    private val dao: ControllerProfileDao
) {
    // Create a dedicated CoroutineScope for this singleton.
    // SupervisorJob() ensures that if one coroutine in this scope fails, it doesn't cancel the whole scope.
    // Dispatchers.Default is appropriate for CPU-bound work like the `.map` operator.
    private val sessionScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)
    val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile.asStateFlow()
    val isControllerLoaded: StateFlow<Boolean> = selectedControllerProfile.map { it != null }
        .stateIn(
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = false,
            scope = sessionScope
        )
    private var previousControllerProfileId: Long? = null

    private val _defaultControllerProfileId = MutableStateFlow(
        value = sharedPreferences?.getLong(KEY_DEFAULT_PROFILE_ID, NO_DEFAULT_ID).let {
            if (it == NO_DEFAULT_ID) null else it
        }
    )
    val defaultControllerProfileId: StateFlow<Long?> = _defaultControllerProfileId.asStateFlow()

    private val _isFirstLoad = MutableStateFlow(value = true)
    val isFirstLoad: StateFlow<Boolean> = _isFirstLoad.asStateFlow()

    suspend fun selectControllerById(id: Long): Boolean {
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

    // @todo: implement controller port in all URL constructions
    open fun getControllerPort(): Int? {
        return selectedControllerProfile.value?.port
    }

    // @todo: check if this function is still required
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