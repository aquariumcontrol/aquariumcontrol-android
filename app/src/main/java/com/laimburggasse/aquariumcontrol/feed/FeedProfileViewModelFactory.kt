package com.laimburggasse.aquariumcontrol.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
class FeedProfileViewModelFactory(
    feedProfiles: List<FeedProfile>?,
) : ViewModelProvider.Factory {
    // This class creates the view model FeedProfileViewModel
    private val viewModel = FeedProfileViewModel(feedProfiles)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}