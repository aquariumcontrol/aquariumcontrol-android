package com.laimburggasse.aquariumcontroller.feed

fun List<FeedProfileImport>.toFeedProfileList(): List<FeedProfile> {
    return this.map { FeedProfile(it) }
}