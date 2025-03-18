package com.laimburggasse.aquariumcontrol.feed

fun List<FeedProfileImport>.toFeedProfileList(): List<FeedProfile> {
    return this.map { FeedProfile(it) }
}