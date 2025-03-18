package com.laimburggasse.aquariumcontrol.feed.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import javax.inject.Singleton

@Singleton
class FeedProfileEditViewModelFactory(
    feedProfile: FeedProfile,
) : ViewModelProvider.Factory {
    // This class creates the view model FeedProfileEditViewModel
    private val viewModel = FeedProfileEditViewModel(feedProfile)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}