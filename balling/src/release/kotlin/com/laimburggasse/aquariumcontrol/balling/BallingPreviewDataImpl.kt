/*
 *  Copyright 2025 Uwe Martin
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.balling

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.presentation.components.BallingPreviewData
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryUiState

object BallingPreviewDataImpl: BallingPreviewData {
    override val mockBallingProfile = BallingProfile(
        pumpId = 0,
        volume = 0.0,
        label = "Release build"
    )
    override val mockBallingProfileEditUiState = BallingProfileEditUiState.Success(mockBallingProfile)

    override val mockBallingProfile1 = mockBallingProfile
    override val mockBallingProfile2 = mockBallingProfile
    override val mockBallingProfiles = emptyList<BallingProfile>()
    override val mockBallingProfilesUiStateSuccess = BallingProfilesUiState.Success(mockBallingProfiles)
    override val mockBallingProfilesUiStateError = BallingProfilesUiState.Error(errorMessage = "Release build")
    override val mockBallingProfilesUiStateLoading = BallingProfilesUiState.Loading
    override val mockBallingProfilesUiStateEmpty = BallingProfilesUiState.Empty

    override val mockBallingHistoryLogEntries = emptyList<BallingHistoryLogEntry>()
    override val mockBallingHistoryUiStateSuccess = BallingHistoryUiState.Success(mockBallingHistoryLogEntries)
    override val mockBallingHistoryUiStateEmpty = BallingHistoryUiState.Empty
    override val mockBallingHistoryUiStateError = BallingHistoryUiState.Error(errorMessage = "Release build")
    override val mockBallingHistoryUiStateLoading = BallingHistoryUiState.Loading
    override val mockBallingEvent = BallingHistoryLogEntry(
        timestamp = "2525",
        pumpId = 0,
        volume = 0.0,
        label = "Release build"
    )
}