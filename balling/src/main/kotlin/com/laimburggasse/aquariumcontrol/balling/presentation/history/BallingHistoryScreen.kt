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

package com.laimburggasse.aquariumcontrol.balling.presentation.history

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewData.mockBallingHistoryLogEntries
import com.laimburggasse.aquariumcontrol.common.presentation.GenericHistoryScreen
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun BallingHistoryScreen(
    ballingHistoryFlow: Flow<PagingData<BallingHistoryLogEntry>>,
    previewData: List<BallingHistoryLogEntry>? = null
) {
    GenericHistoryScreen(
        historyFlow = ballingHistoryFlow,
        headline = stringResource(id = R.string.balling_log),
        noEventsMessage = stringResource(R.string.no_balling_events_found),
        entryComposable = { ballingHistoryLogEntry ->
            BallingHistoryLogEntryComposable(ballingHistoryLogEntry)
        },
        previewData = previewData
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingHistoryScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory,
        screenContent = {
            BallingHistoryScreen(
                ballingHistoryFlow = flowOf(PagingData.from(
                    mockBallingHistoryLogEntries,
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true)
                    )
                )),
                previewData = mockBallingHistoryLogEntries
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingHistoryScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory,
        screenContent = {
            BallingHistoryScreen(
                ballingHistoryFlow = flowOf(PagingData.from(
                    emptyList(),
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true)
                    )
                )),
                previewData = mockBallingHistoryLogEntries
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenLoadingDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory,
        screenContent = {
            BallingHistoryScreen(
                ballingHistoryFlow = flowOf(PagingData.from(emptyList(), LoadStates(
                    refresh = LoadState.Loading,
                    prepend = LoadState.NotLoading(true),
                    append = LoadState.NotLoading(true)
                ))),
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryScreenLoadingLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory,
        screenContent = {
            BallingHistoryScreen(
                ballingHistoryFlow = flowOf(PagingData.from(emptyList(), LoadStates(
                    refresh = LoadState.Loading,
                    prepend = LoadState.NotLoading(true),
                    append = LoadState.NotLoading(true)
                ))),
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}