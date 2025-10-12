package com.laimburggasse.aquariumcontrol.feed.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesRepository
import javax.inject.Singleton

@Singleton
class FeedScheduleViewModelFactory(
    feedScheduleRepository: FeedScheduleRepository,
    feedProfilesRepository: FeedProfilesRepository
) : ViewModelProvider.Factory {
    // This class creates the view model FeedScheduleViewModel
    private val viewModel = FeedScheduleViewModel(feedScheduleRepository, feedProfilesRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}