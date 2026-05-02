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

package com.laimburggasse.aquariumcontrol.feed.testing

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileChartDataBundle
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileChartDataFactory
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditMenuItem
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditPhaseType
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditToggleButtonType
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleSelectionUiState

fun createMockFeedProfile(): FeedProfile {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.pause01Duration = 1000
    mockFeedProfileImport.pause02Duration = 1000
    mockFeedProfileImport.pause03Duration = 1000
    mockFeedProfileImport.pause04Duration = 1000
    mockFeedProfileImport.pause05Duration = 1000
    mockFeedProfileImport.pause06Duration = 1000
    mockFeedProfileImport.pause07Duration = 1000
    mockFeedProfileImport.pause08Duration = 1000
    mockFeedProfileImport.pause09Duration = 1000
    mockFeedProfileImport.pause10Duration = 1000
    mockFeedProfileImport.feed01Duration = 1000
    mockFeedProfileImport.feed02Duration = 1000
    mockFeedProfileImport.feed03Duration = 1000
    mockFeedProfileImport.feed04Duration = 1000
    mockFeedProfileImport.feed05Duration = 1000
    mockFeedProfileImport.feed06Duration = 1000
    mockFeedProfileImport.feed07Duration = 1000
    mockFeedProfileImport.feed08Duration = 1000
    mockFeedProfileImport.feed09Duration = 1000
    mockFeedProfileImport.feed10Duration = 1000
    mockFeedProfileImport.pause01Skimmer = 1
    mockFeedProfileImport.pause02Skimmer = 1
    mockFeedProfileImport.pause03Skimmer = 1
    mockFeedProfileImport.pause04Skimmer = 1
    mockFeedProfileImport.pause05Skimmer = 1
    mockFeedProfileImport.pause06Skimmer = 1
    mockFeedProfileImport.pause07Skimmer = 1
    mockFeedProfileImport.pause08Skimmer = 1
    mockFeedProfileImport.pause09Skimmer = 1
    mockFeedProfileImport.pause10Skimmer = 0
    mockFeedProfileImport.feed01Skimmer = 1
    mockFeedProfileImport.feed02Skimmer = 1
    mockFeedProfileImport.feed03Skimmer = 1
    mockFeedProfileImport.feed04Skimmer = 1
    mockFeedProfileImport.feed05Skimmer = 1
    mockFeedProfileImport.feed06Skimmer = 1
    mockFeedProfileImport.feed07Skimmer = 1
    mockFeedProfileImport.feed08Skimmer = 1
    mockFeedProfileImport.feed09Skimmer = 1
    mockFeedProfileImport.feed10Skimmer = 0
    mockFeedProfileImport.pause01MainPump1 = 1
    mockFeedProfileImport.pause02MainPump1 = 1
    mockFeedProfileImport.pause03MainPump1 = 1
    mockFeedProfileImport.pause04MainPump1 = 1
    mockFeedProfileImport.pause05MainPump1 = 1
    mockFeedProfileImport.pause06MainPump1 = 1
    mockFeedProfileImport.pause07MainPump1 = 1
    mockFeedProfileImport.pause08MainPump1 = 1
    mockFeedProfileImport.pause09MainPump1 = 0
    mockFeedProfileImport.pause10MainPump1 = 0
    mockFeedProfileImport.feed01MainPump1 = 1
    mockFeedProfileImport.feed02MainPump1 = 1
    mockFeedProfileImport.feed03MainPump1 = 1
    mockFeedProfileImport.feed04MainPump1 = 1
    mockFeedProfileImport.feed05MainPump1 = 1
    mockFeedProfileImport.feed06MainPump1 = 1
    mockFeedProfileImport.feed07MainPump1 = 1
    mockFeedProfileImport.feed08MainPump1 = 1
    mockFeedProfileImport.feed09MainPump1 = 0
    mockFeedProfileImport.feed10MainPump1 = 0
    mockFeedProfileImport.pause01MainPump2 = 1
    mockFeedProfileImport.pause02MainPump2 = 1
    mockFeedProfileImport.pause03MainPump2 = 1
    mockFeedProfileImport.pause04MainPump2 = 1
    mockFeedProfileImport.pause05MainPump2 = 1
    mockFeedProfileImport.pause06MainPump2 = 1
    mockFeedProfileImport.pause07MainPump2 = 1
    mockFeedProfileImport.pause08MainPump2 = 0
    mockFeedProfileImport.pause09MainPump2 = 0
    mockFeedProfileImport.pause10MainPump2 = 0
    mockFeedProfileImport.feed01MainPump2 = 1
    mockFeedProfileImport.feed02MainPump2 = 1
    mockFeedProfileImport.feed03MainPump2 = 1
    mockFeedProfileImport.feed04MainPump2 = 1
    mockFeedProfileImport.feed05MainPump2 = 1
    mockFeedProfileImport.feed06MainPump2 = 1
    mockFeedProfileImport.feed07MainPump2 = 1
    mockFeedProfileImport.feed08MainPump2 = 0
    mockFeedProfileImport.feed09MainPump2 = 0
    mockFeedProfileImport.feed10MainPump2 = 0
    mockFeedProfileImport.pause01AuxPump1 = 1
    mockFeedProfileImport.pause02AuxPump1 = 1
    mockFeedProfileImport.pause03AuxPump1 = 1
    mockFeedProfileImport.pause04AuxPump1 = 1
    mockFeedProfileImport.pause05AuxPump1 = 1
    mockFeedProfileImport.pause06AuxPump1 = 1
    mockFeedProfileImport.pause07AuxPump1 = 0
    mockFeedProfileImport.pause08AuxPump1 = 0
    mockFeedProfileImport.pause09AuxPump1 = 0
    mockFeedProfileImport.pause10AuxPump1 = 0
    mockFeedProfileImport.feed01AuxPump1 = 1
    mockFeedProfileImport.feed02AuxPump1 = 1
    mockFeedProfileImport.feed03AuxPump1 = 1
    mockFeedProfileImport.feed04AuxPump1 = 1
    mockFeedProfileImport.feed05AuxPump1 = 1
    mockFeedProfileImport.feed06AuxPump1 = 1
    mockFeedProfileImport.feed07AuxPump1 = 0
    mockFeedProfileImport.feed08AuxPump1 = 0
    mockFeedProfileImport.feed09AuxPump1 = 0
    mockFeedProfileImport.feed10AuxPump1 = 0
    mockFeedProfileImport.pause01AuxPump2 = 1
    mockFeedProfileImport.pause02AuxPump2 = 1
    mockFeedProfileImport.pause03AuxPump2 = 1
    mockFeedProfileImport.pause04AuxPump2 = 1
    mockFeedProfileImport.pause05AuxPump2 = 1
    mockFeedProfileImport.pause06AuxPump2 = 1
    mockFeedProfileImport.pause07AuxPump2 = 0
    mockFeedProfileImport.pause08AuxPump2 = 0
    mockFeedProfileImport.pause09AuxPump2 = 0
    mockFeedProfileImport.pause10AuxPump2 = 0
    mockFeedProfileImport.feed01AuxPump2 = 1
    mockFeedProfileImport.feed02AuxPump2 = 1
    mockFeedProfileImport.feed03AuxPump2 = 1
    mockFeedProfileImport.feed04AuxPump2 = 1
    mockFeedProfileImport.feed05AuxPump2 = 1
    mockFeedProfileImport.feed06AuxPump2 = 1
    mockFeedProfileImport.feed07AuxPump2 = 0
    mockFeedProfileImport.feed08AuxPump2 = 0
    mockFeedProfileImport.feed09AuxPump2 = 0
    mockFeedProfileImport.feed10AuxPump2 = 0

    return FeedProfile.fromFeedProfileImport(mockFeedProfileImport)
}

fun createMockFeedProfiles(): List<FeedProfile> {
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

    val mockFeedProfile1: FeedProfile = FeedProfile.fromFeedProfileImport(feedProfileImport = mockFeedProfileImport1)
    val mockFeedProfile2: FeedProfile = FeedProfile.fromFeedProfileImport(feedProfileImport = mockFeedProfileImport2)

    return listOf(mockFeedProfile1, mockFeedProfile2)
}

fun createMockFeedProfileImports(): List<FeedProfileImport> {
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

    return listOf(mockFeedProfileImport1, mockFeedProfileImport2)
}

fun createMockFeedScheduleEntries(): List<FeedScheduleEntry> {
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

    // create mock feed schedule entries
    val mockFeedScheduleEntry1 = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileId = 1,
        profileName = "Test Profile 1",
        isDaily = 1
    ).transformToFeedScheduleEntry()

    val mockFeedScheduleEntry2 = FeedScheduleEntryImport(
        timestampString = "2023-09-01 13:00:00",
        profileId = 2,
        profileName = "Test Profile 2",
        isDaily = 0
    ).transformToFeedScheduleEntry()

    return listOf(mockFeedScheduleEntry1, mockFeedScheduleEntry2)
}

fun createMockFeedScheduleEntryImports(): List<FeedScheduleEntryImport> {
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

    // create mock feed schedule entries
    val mockFeedScheduleEntryImport1 = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileId = 1,
        profileName = "Test Profile 1",
        isDaily = 1
    )

    val mockFeedScheduleEntryImport2 = FeedScheduleEntryImport(
        timestampString = "2023-09-01 13:00:00",
        profileId = 2,
        profileName = "Test Profile 2",
        isDaily = 0
    )

    return listOf(mockFeedScheduleEntryImport1, mockFeedScheduleEntryImport2)
}

object FeedMockData {
    val mockFeedProfileForSimpleDisplayComposable: FeedProfile = FeedProfile().apply {
        feedProfilePhases[0].duration = 5.0
        feedProfilePhases[1].duration = 10.0
        feedProfilePhases[1].skimmer = true
    }
    val mockChartData: FeedProfileChartDataBundle = FeedProfileChartDataFactory.create(
        mockFeedProfileForSimpleDisplayComposable
    )
    val mockInitialExpandedItemIds: Set<Int> = setOf(1)
    val mockInitialExpandedItemIdsEmpty: Set<Int> = emptySet()
    val mockFeedProfileEditNumericMenuItem = FeedProfileEditMenuItem(
        id = 100,
        level = 3,
        text = "Preview",
        isToggleButton = true,
        children = emptyList(),
        phaseType = FeedProfileEditPhaseType.FEED,
        toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
        isNumericInput = false,
    )
    val mockFeedProfile = createMockFeedProfile()
    val mockFeedProfileEditUiStateSuccess = FeedProfileEditUiState(
        feedProfile = mockFeedProfile,
    )
    val mockFeedProfileEditUiStateFailure = FeedProfileEditUiState(
        feedProfile = null,
    )
    val mockFeedScheduleSelectionUiStateEmpty = FeedScheduleSelectionUiState.Empty
    val mockFeedProfiles = createMockFeedProfiles()
    val mockFeedProfilesUiStateSuccess = FeedProfilesUiState.Success(
        feedProfiles = mockFeedProfiles
    )
    val mockFeedProfilesUiStateError = FeedProfilesUiState.Error(errorMessage = "Mock Error")
    val mockFeedScheduleSelectionUiStateSuccess = FeedScheduleSelectionUiState.Success(
        feedScheduleEntries = createMockFeedScheduleEntries()
    )
    val mockFeedScheduleSelectionUiStateError = FeedScheduleSelectionUiState.Error(errorMessage = "Mock Error")
    val mockEvent = FeedHistoryLogEntry(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 20000,
        profileName = "Mock feed profile 1",
        idx = 1
    )
    val mockFeedHistoryLogEntries = listOf(
        FeedHistoryLogEntry(
            timestamp = "2025-01-18 12:00:00",
            feederOnTime = 20000,
            profileName = "Mock feed profile 1",
            idx = 1
        ),
        FeedHistoryLogEntry(
            timestamp = "2025-01-18 12:00:00",
            feederOnTime = 10000,
            profileName = "Mock feed profile 2",
            idx = 2
        ),
    )
    val mockFeedProfileImportForScheduleScreen = FeedProfileImport()
    val mockFeedProfileForScheduleScreen: FeedProfile = FeedProfile.fromFeedProfileImportUpdateIdName(
        feedProfileImport = mockFeedProfileImportForScheduleScreen,
        profileId = 1,
        profileName = "Test Profile 1"
    )
    val mockFeedScheduleEntryImport = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileId = 1,
        profileName = "MockProfile",
        isDaily = 1
    )
    val mockFeedScheduleEntry = mockFeedScheduleEntryImport.transformToFeedScheduleEntry()
}