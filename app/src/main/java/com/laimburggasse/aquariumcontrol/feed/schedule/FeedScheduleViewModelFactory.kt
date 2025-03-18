package com.laimburggasse.aquariumcontrol.feed.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class FeedScheduleViewModelFactory(
    feedScheduleEntries: List<FeedScheduleEntry>?,
) : ViewModelProvider.Factory {
    // This class creates the view model FeedScheduleViewModel
    private val viewModel = FeedScheduleViewModel(feedScheduleEntries)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}