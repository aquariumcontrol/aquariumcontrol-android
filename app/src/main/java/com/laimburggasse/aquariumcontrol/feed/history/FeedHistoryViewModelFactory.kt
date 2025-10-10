package com.laimburggasse.aquariumcontrol.feed.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FeedHistoryViewModelFactory(
    feedHistoryRepository: FeedHistoryRepository
) : ViewModelProvider.Factory {

    // This class create the view model FeedHistoryViewModel
    private val viewModel = FeedHistoryViewModel(feedHistoryRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}