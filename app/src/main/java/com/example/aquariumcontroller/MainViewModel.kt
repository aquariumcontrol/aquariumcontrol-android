package com.example.aquariumcontroller

//import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.balling.BallingProfile
import com.example.aquariumcontroller.controller.ControllerProfile
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntry

class MainViewModel: ViewModel() {
    private var defaultControllerProfileId: Long = 0

    fun getDefaultControllerProfileId(): Long {
        //Log.d("MainViewModel", "Getting default controller profile id: $defaultControllerProfileId")
        return defaultControllerProfileId
    }

    fun setDefaultControllerProfileId(id: Long) {
        //Log.d("MainViewModel", "Setting default controller profile id: $id")
        this.defaultControllerProfileId = id
    }

    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        selectedController = controllerProfile
    }

    fun updateSelectedBallingProfile(ballingProfile: BallingProfile) {
        selectedBallingProfile = ballingProfile
        //Log.d("MainViewModel", "Selected balling profile: ${ballingProfile.label}")
    }

    fun isControllerLoaded(): Boolean {
        return selectedController != null
    }

    fun getControllerAddress(): String? {
        return selectedController?.address
    }

    fun getControllerUser(): String? {
        return selectedController?.user
    }

    fun getControllerPassword(): String? {
        return selectedController?.password
    }

    fun getControllerPort(): Int? {
        return selectedController?.port
    }

    fun getControllerName(): String? {
        return selectedController?.name
    }

    private var selectedController: ControllerProfile? by mutableStateOf(null)

    fun getMySelectedBallingProfile(): BallingProfile? {
        return selectedBallingProfile
    }

    private var selectedBallingProfile: BallingProfile? by mutableStateOf(null)

    fun getMySelectedFeedProfile(): FeedProfile? {
        return selectedFeedProfile
    }

    fun updateSelectedFeedProfile(feedProfile: FeedProfile) {
        selectedFeedProfile = feedProfile
    }

    private var selectedFeedProfile: FeedProfile? by mutableStateOf(null)

    fun getMySelectedFeedScheduleEntry(): FeedScheduleEntry? {
        return selectedFeedScheduleEntry
    }

    fun updateSelectedFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry?) {
        selectedFeedScheduleEntry = feedScheduleEntry
    }

    private var selectedFeedScheduleEntry: FeedScheduleEntry? by mutableStateOf(null)
}