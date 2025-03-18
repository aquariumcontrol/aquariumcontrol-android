package com.laimburggasse.aquariumcontrol.feed.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import javax.inject.Singleton

@Singleton
class FeedProfileAddViewModelFactory(
    feedProfile: FeedProfile,
) : ViewModelProvider.Factory {
    // This class creates the view model FeedProfileAddViewModel
    private val viewModel = FeedProfileAddViewModel(feedProfile)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}