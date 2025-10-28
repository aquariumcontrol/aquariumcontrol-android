package com.laimburggasse.aquariumcontrol

import android.content.SharedPreferences
import android.util.Log
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

open class SessionManager(
    private val sharedPreferences: SharedPreferences? = null,
    private val dao: ControllerProfileDao
) {
    // Create a dedicated CoroutineScope for this singleton.
    // SupervisorJob() ensures that if one coroutine in this scope fails, it doesn't cancel the whole scope.
    // Dispatchers.Default is appropriate for CPU-bound work like the `.map` operator.

    init {
        Log.d("SessionManager_LIFECYCLE", "A new SessionManager instance has been CREATED. HashCode: ${this.hashCode()}")
    }
    private val sessionScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    private val _selectedControllerProfile = MutableStateFlow<ControllerProfile?>(value = null)
    val selectedControllerProfile: StateFlow<ControllerProfile?> = _selectedControllerProfile.asStateFlow()
    val isControllerLoaded: StateFlow<Boolean> = selectedControllerProfile.map { it != null }
        .onEach { // debug @todo: remove
            isLoaded -> Log.d("SessionManager", "isControllerLoaded: $isLoaded")
        }
        .stateIn(
            started = SharingStarted.Eagerly,
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
        Log.d("SessionManager", "selectControllerById: $id")
        previousControllerProfileId = _defaultControllerProfileId.value
        return withContext(context = Dispatchers.IO) {
            val profile: ControllerProfile? = dao.getControllerProfileById(id)
            if (profile != null) {
                Log.d("SessionManager", "selectControllerById found profile: $profile")
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
        Log.d("SessionManager", "updateSelectedControllerProfile: $controllerProfile")
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
        Log.d("SessionManager", "unsetDefaultControllerId")
        if (previousControllerProfileId != null) {
            _defaultControllerProfileId.value = previousControllerProfileId!!
        }
    }
}