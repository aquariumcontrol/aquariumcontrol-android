package com.laimburggasse.aquariumcontrol

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.laimburggasse.aquariumcontrol.balling.BallingProfile
import com.laimburggasse.aquariumcontrol.controller.ControllerProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel

class MainViewModel: ViewModel() {
    private var selectedController: ControllerProfile? by mutableStateOf(null)
    private var defaultControllerProfileId: Long = 0
    private var selectedFeedScheduleEntry: FeedScheduleEntry? by mutableStateOf(null)
    private var firstLoad: Boolean = true
    private var feedScheduleViewModel: FeedScheduleViewModel? by mutableStateOf(null)

    fun getDefaultControllerProfileId(): Long {
        return defaultControllerProfileId
    }

    fun setDefaultControllerProfileId(id: Long) {
        this.defaultControllerProfileId = id
    }

    fun updateSelectedControllerProfile(controllerProfile: ControllerProfile) {
        selectedController = controllerProfile
    }

    fun updateSelectedBallingProfile(ballingProfile: BallingProfile) {
        selectedBallingProfile = ballingProfile
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

    fun isFirstLoad(): Boolean {
        return firstLoad
    }

    fun setFirstLoad() {
        firstLoad = false
    }

    fun getMyFeedScheduleViewModel(): FeedScheduleViewModel? {
        return feedScheduleViewModel
    }

    fun setMyFeedScheduleViewModel(feedScheduleViewModel: FeedScheduleViewModel?) {
        this.feedScheduleViewModel = feedScheduleViewModel
    }
}