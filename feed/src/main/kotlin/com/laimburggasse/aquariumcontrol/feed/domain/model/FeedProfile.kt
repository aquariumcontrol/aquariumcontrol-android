package com.laimburggasse.aquariumcontrol.feed.domain.model

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams

data class FeedProfile(
    val profileID: Int,
    val profileName: String,
    val feedProfilePhases: List<FeedProfilePhase>,
) {
    constructor() : this(
        profileID = 0,
        profileName = "",
        // Creates a list of 20 default phases (10 pause, 10 feed)
        feedProfilePhases = List(size = 20) { index ->
            FeedProfilePhase(
                duration = 0.0,
                skimmer = false,
                mainPump1 = false,
                mainPump2 = false,
                auxPump1 = false,
                auxPump2 = false,
                isPause = index % 2 == 0, // Even indices are pause phases, uneven are feed phases
                idx = index
            )
        }
    )

    companion object {
        fun fromFeedProfileImportUpdateIdName(
            feedProfileImport: FeedProfileImport,
            profileId: Int,
            profileName: String
        ): FeedProfile {
            feedProfileImport.profileId = profileId
            feedProfileImport.profileName = profileName
            return fromFeedProfileImport(feedProfileImport)
        }

        fun fromFeedProfileImport(feedProfileImport: FeedProfileImport): FeedProfile {
            val phases: List<FeedProfilePhase> = List(size = 20) { index ->
                val isPause: Boolean = index % 2 == 0
                val phaseNum: Int = (index / 2) + 1 // e.g., 0,1 -> 1; 2,3 -> 2
                val importPrefix: String = if (isPause) "pause%02d".format(phaseNum) else "feed%02d".format(phaseNum)

                // This is a more advanced way using reflection to avoid the massive wall of text.
                // It dynamically gets the property from the import object.
                fun <T> getImportProperty(name: String, default: T): T {
                    return try {
                        @Suppress("UNCHECKED_CAST")
                        feedProfileImport::class.java.getDeclaredField(name).let { field ->
                            field.isAccessible = true
                            (field.get(feedProfileImport) as? T) ?: default
                        }
                    } catch (_: Exception) {
                        default
                    }
                }

                fun getImportBoolProperty(name: String): Boolean {
                    return try {
                        feedProfileImport::class.java.getDeclaredField(name).let { field ->
                            field.isAccessible = true
                            (field.get(feedProfileImport) as Boolean)
                        }
                    } catch (_: Exception) {
                        false
                    }
                }

                FeedProfilePhase(
                    duration = getImportProperty(name = "${importPrefix}duration", 0.0) / 1000.0f,
                    skimmer = getImportBoolProperty(name = "${importPrefix}skimmer"),
                    mainPump1 = getImportBoolProperty(name = "${importPrefix}mainPump1"),
                    mainPump2 = getImportBoolProperty(name = "${importPrefix}mainPump2"),
                    auxPump1 = getImportBoolProperty(name = "${importPrefix}auxPump1"),
                    auxPump2 = getImportBoolProperty(name = "${importPrefix}auxPump2"),
                    isPause = isPause,
                    idx = index
                )
            }

            return FeedProfile(
                profileID = feedProfileImport.profileId,
                profileName = feedProfileImport.profileName,
                feedProfilePhases = phases
            )
        }
    }
    fun getFeedProfileApiCallParameters(): Map<String, String> {
        // This function iterates over the feed profile phases and adds all the parameters to the
        // map which is then used to form the GET part of the API call
        val myMap: MutableMap<String, String> = mutableMapOf()
        for ((idx: Int, feedProfilePhase: FeedProfilePhase) in feedProfilePhases.withIndex()) {
            myMap += feedProfilePhase.getFeedProfilePhaseApiCallParameters(
                idx = (idx.toFloat() / 2.0).toInt()
            )
        }
        myMap += mapOf(
            WebApiParams.PROFILE_NAME to this.profileName,
            WebApiParams.PROFILE_ID to this.profileID.toString()
        )
        return myMap
    }
}