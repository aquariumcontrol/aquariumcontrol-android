package com.laimburggasse.aquariumcontrol.refill.testing

import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import com.laimburggasse.aquariumcontrol.refill.presentation.history.RefillHistoryUiState
import java.sql.Timestamp

object RefillMockData {
    val mockFixedTimestamp = Timestamp(0L)
    val mockEvent = RefillHistoryLogEntry(
        timestamp = mockFixedTimestamp,
        duration = 60.0,
        volume = 10.0
    )
    val mockRefillHistoryLogEntries = listOf(
        RefillHistoryLogEntry(
            timestamp = Timestamp(0L),
            duration = 60.0,
            volume = 10.0
        ),
        RefillHistoryLogEntry(
            timestamp = Timestamp(1000L),
            duration = 30.0,
            volume = 5.0
        )
    )
    val mockRefillHistoryUiStateSuccess = RefillHistoryUiState.Success(
        refillHistoryLogEntries = mockRefillHistoryLogEntries
    )
    val mockRefillHistoryUiStateEmpty = RefillHistoryUiState.Empty
    val mockRefillHistoryUiStateError = RefillHistoryUiState.Error(
        errorMessage = "Mock Error message"
    )
}