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

package com.laimburggasse.aquariumcontrol.balling.preview

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesUiState

@Suppress("unused")
object BallingPreviewData {
    val mockBallingProfile: BallingProfile get() = error("Preview only")
    val mockBallingProfileEditUiState: BallingProfileEditUiState.Success get() = error("Preview only")
    val mockBallingProfilesUiStateSuccess: BallingProfilesUiState.Success get() = error("Preview only")
    val mockBallingProfilesUiStateError: BallingProfilesUiState.Error get() = error("Preview only")
    val mockBallingProfilesUiStateLoading: BallingProfilesUiState.Loading get() = error("Preview only")
    val mockBallingProfilesUiStateEmpty: BallingProfilesUiState.Empty get() = error("Preview only")
    val mockBallingHistoryLogEntries: List<BallingHistoryLogEntry> get() = error("Preview only")
    val mockBallingEvent: BallingHistoryLogEntry get() = error("Preview only")
}
