/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.domain.model

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport

/**
 * Mapper object for converting between feed profile DTOs and domain models.
 */
object FeedProfileMapper {
        /**
     * Maps an import DTO to a domain model while manually overriding the ID and Name.
     * Useful when creating new profiles from templates or handling temporary objects.
     *
     * @param feedProfileImport The DTO to map.
     * @param profileId The profile ID to assign.
     * @param profileName The profile name to assign.
     * @return A [FeedProfile] domain model.
     */
    fun fromFeedProfileImportUpdateIdName(
        feedProfileImport: FeedProfileImport,
        profileId: Int,
        profileName: String
    ): FeedProfile {
        feedProfileImport.profileId = profileId
        feedProfileImport.profileName = profileName
        return fromFeedProfileImport(feedProfileImport)
    }

        /**
     * Maps a [FeedProfileImport] DTO into a structured [FeedProfile] domain model.
     * Expands the flat phase properties of the DTO into the list of [FeedProfilePhase] objects.
     *
     * @param imp The import DTO containing flat phase properties.
     * @return A [FeedProfile] containing a list of 20 phases (10 pairs of pause/feed).
     */
    fun fromFeedProfileImport(imp: FeedProfileImport): FeedProfile {
        val phases = mutableListOf<FeedProfilePhase>()

        fun addPhase(index: Int, isPause: Boolean, duration: Int?, skimmer: Int, mp1: Int, mp2: Int, ap1: Int, ap2: Int) {
            phases.add(
                FeedProfilePhase(
                    duration = (duration ?: 0).toDouble() / 1000.0,
                    skimmer = skimmer != 0,
                    mainPump1 = mp1 != 0,
                    mainPump2 = mp2 != 0,
                    auxPump1 = ap1 != 0,
                    auxPump2 = ap2 != 0,
                    isPause = isPause,
                    idx = index
                )
            )
        }

        // Phase 1
        addPhase(0, true, imp.pause01Duration, imp.pause01Skimmer, imp.pause01MainPump1, imp.pause01MainPump2, imp.pause01AuxPump1, imp.pause01AuxPump2)
        addPhase(1, false, imp.feed01Duration, imp.feed01Skimmer, imp.feed01MainPump1, imp.feed01MainPump2, imp.feed01AuxPump1, imp.feed01AuxPump2)
        // Phase 2
        addPhase(2, true, imp.pause02Duration, imp.pause02Skimmer, imp.pause02MainPump1, imp.pause02MainPump2, imp.pause02AuxPump1, imp.pause02AuxPump2)
        addPhase(3, false, imp.feed02Duration, imp.feed02Skimmer, imp.feed02MainPump1, imp.feed02MainPump2, imp.feed02AuxPump1, imp.feed02AuxPump2)
        // Phase 3
        addPhase(4, true, imp.pause03Duration, imp.pause03Skimmer, imp.pause03MainPump1, imp.pause03MainPump2, imp.pause03AuxPump1, imp.pause03AuxPump2)
        addPhase(5, false, imp.feed03Duration, imp.feed03Skimmer, imp.feed03MainPump1, imp.feed03MainPump2, imp.feed03AuxPump1, imp.feed03AuxPump2)
        // Phase 4
        addPhase(6, true, imp.pause04Duration, imp.pause04Skimmer, imp.pause04MainPump1, imp.pause04MainPump2, imp.pause04AuxPump1, imp.pause04AuxPump2)
        addPhase(7, false, imp.feed04Duration, imp.feed04Skimmer, imp.feed04MainPump1, imp.feed04MainPump2, imp.feed04AuxPump1, imp.feed04AuxPump2)
        // Phase 5
        addPhase(8, true, imp.pause05Duration, imp.pause05Skimmer, imp.pause05MainPump1, imp.pause05MainPump2, imp.pause05AuxPump1, imp.pause05AuxPump2)
        addPhase(9, false, imp.feed05Duration, imp.feed05Skimmer, imp.feed05MainPump1, imp.feed05MainPump2, imp.feed05AuxPump1, imp.feed05AuxPump2)
        // Phase 6
        addPhase(10, true, imp.pause06Duration, imp.pause06Skimmer, imp.pause06MainPump1, imp.pause06MainPump2, imp.pause06AuxPump1, imp.pause06AuxPump2)
        addPhase(11, false, imp.feed06Duration, imp.feed06Skimmer, imp.feed06MainPump1, imp.feed06MainPump2, imp.feed06AuxPump1, imp.feed06AuxPump2)
        // Phase 7
        addPhase(12, true, imp.pause07Duration, imp.pause07Skimmer, imp.pause07MainPump1, imp.pause07MainPump2, imp.pause07AuxPump1, imp.pause07AuxPump2)
        addPhase(13, false, imp.feed07Duration, imp.feed07Skimmer, imp.feed07MainPump1, imp.feed07MainPump2, imp.feed07AuxPump1, imp.feed07AuxPump2)
        // Phase 8
        addPhase(14, true, imp.pause08Duration, imp.pause08Skimmer, imp.pause08MainPump1, imp.pause08MainPump2, imp.pause08AuxPump1, imp.pause08AuxPump2)
        addPhase(15, false, imp.feed08Duration, imp.feed08Skimmer, imp.feed08MainPump1, imp.feed08MainPump2, imp.feed08AuxPump1, imp.feed08AuxPump2)
        // Phase 9
        addPhase(16, true, imp.pause09Duration, imp.pause09Skimmer, imp.pause09MainPump1, imp.pause09MainPump2, imp.pause09AuxPump1, imp.pause09AuxPump2)
        addPhase(17, false, imp.feed09Duration, imp.feed09Skimmer, imp.feed09MainPump1, imp.feed09MainPump2, imp.feed09AuxPump1, imp.feed09AuxPump2)
        // Phase 10
        addPhase(18, true, imp.pause10Duration, imp.pause10Skimmer, imp.pause10MainPump1, imp.pause10MainPump2, imp.pause10AuxPump1, imp.pause10AuxPump2)
        addPhase(19, false, imp.feed10Duration, imp.feed10Skimmer, imp.feed10MainPump1, imp.feed10MainPump2, imp.feed10AuxPump1, imp.feed10AuxPump2)

        return FeedProfile(
            profileID = imp.profileId,
            profileName = imp.profileName,
            feedProfilePhases = phases
        )
    }
}
