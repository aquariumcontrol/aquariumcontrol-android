package com.laimburggasse.aquariumcontrol.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class FeedProfilesViewModelFactory(
    feedProfilesRepository: FeedProfilesRepository
) : ViewModelProvider.Factory {
    // This class creates the view model FeedProfilesViewModel
    private val viewModel = FeedProfilesViewModel(feedProfilesRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}