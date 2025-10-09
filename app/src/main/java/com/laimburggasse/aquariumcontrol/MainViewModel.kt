package com.laimburggasse.aquariumcontrol

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.laimburggasse.aquariumcontrol.balling.BallingProfile
import com.laimburggasse.aquariumcontrol.controller.ControllerProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesViewModelInterface
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModelInterface

open class MainViewModel: ViewModel() {
    private var selectedController: ControllerProfile? by mutableStateOf(null)
    private var defaultControllerProfileId: Long = 0
    private var selectedFeedScheduleEntry: FeedScheduleEntry? by mutableStateOf(null)
    private var firstLoad: Boolean = true
    private var feedScheduleViewModel: FeedScheduleViewModelInterface? by mutableStateOf(null)

    private var feedProfilesViewModel: FeedProfilesViewModelInterface? by mutableStateOf(null)

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

    open fun getControllerAddress(): String? {
        return selectedController?.address
    }

    open fun getControllerUser(): String? {
        return selectedController?.user
    }

    fun getControllerPassword(): String? {
        return selectedController?.password
    }

    open fun getControllerPort(): Int? {
        return selectedController?.port
    }

    open fun getControllerName(): String? {
        return selectedController?.name
    }

    fun getMySelectedBallingProfile(): BallingProfile? {
        return selectedBallingProfile
    }

    private var selectedBallingProfile: BallingProfile? by mutableStateOf(null)

    fun getMySelectedFeedProfile(): FeedProfile? {
        return selectedFeedProfile
    }

    fun updateSelectedFeedProfile(feedProfile: FeedProfile?) {
        selectedFeedProfile = feedProfile
    }

    private var selectedFeedProfile: FeedProfile? by mutableStateOf(null)

    fun updateSelectedFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry?) {
        selectedFeedScheduleEntry = feedScheduleEntry
    }

    fun isFirstLoad(): Boolean {
        return firstLoad
    }

    fun setFirstLoad() {
        firstLoad = false
    }

    fun getMyFeedScheduleViewModel(): FeedScheduleViewModelInterface? {
        return feedScheduleViewModel
    }

    fun setMyFeedScheduleViewModel(feedScheduleViewModel: FeedScheduleViewModelInterface) {
        this.feedScheduleViewModel = feedScheduleViewModel
    }

    fun getMyFeedProfilesViewModel(): FeedProfilesViewModelInterface? {
        return feedProfilesViewModel
    }

    fun setMyFeedProfileViewModel(feedProfilesViewModel: FeedProfilesViewModelInterface) {
        this.feedProfilesViewModel = feedProfilesViewModel
    }

}