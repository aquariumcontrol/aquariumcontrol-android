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

package com.laimburggasse.aquariumcontrol.balling.testing

import androidx.paging.PagingData
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
        ballingHistoryLogEntries = PagingData.from(mockBallingHistoryLogEntries)
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