package com.laimburggasse.aquariumcontrol.di

import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object FeedProfileModule {

    @Provides
    fun provideFeedProfile(): FeedProfile {
        // Provide a default or mock instance, or load from somewhere
        return FeedProfile()
    }
}