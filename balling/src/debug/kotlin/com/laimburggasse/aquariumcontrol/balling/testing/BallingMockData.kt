package com.laimburggasse.aquariumcontrol.balling.testing

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryUiState

object BallingMockData {
    val mockBallingProfile = BallingProfile(
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1",
    )

    val mockBallingProfileEditUiState = BallingProfileEditUiState.Success(
        ballingProfile = mockBallingProfile
    )

    val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
    val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
    val mockBallingProfiles: List<BallingProfile> = listOf(mockBallingProfile1, mockBallingProfile2)

    val mockBallingProfilesUiStateSuccess = BallingProfilesUiState.Success(ballingProfiles = mockBallingProfiles)
    val mockBallingProfilesUiStateError = BallingProfilesUiState.Error(errorMessage = "Mock Error message")
    val mockBallingProfilesUiStateLoading = BallingProfilesUiState.Loading
    val mockBallingProfilesUiStateEmpty = BallingProfilesUiState.Empty
    val mockBallingHistoryLogEntries = listOf(
        BallingHistoryLogEntry(
            timestamp = "2025-01-18 12:00:00",
            pumpId = 1,
            volume = 1.5,
            label = "MockDosingUnit 1"
        ),
        BallingHistoryLogEntry(
            timestamp = "2025-01-18 12:00:00",
            pumpId = 2,
            volume = 2.5,
            label = "MockDosingUnit 2"
        ),
    )
    val mockBallingHistoryUiStateSuccess = BallingHistoryUiState.Success(
        ballingHistoryLogEntries = mockBallingHistoryLogEntries
    )
    val mockBallingHistoryUiStateEmpty = BallingHistoryUiState.Empty
    val mockBallingHistoryUiStateError = BallingHistoryUiState.Error(errorMessage = "Mock Error message")
    val mockBallingHistoryUiStateLoading = BallingHistoryUiState.Loading
    val mockBallingEvent = BallingHistoryLogEntry(
        timestamp = "2025-01-18 12:00:00",
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1"
    )
}