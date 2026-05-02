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

package com.laimburggasse.aquariumcontrol.refill.presentation.history

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.refill.R
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import com.laimburggasse.aquariumcontrol.refill.testing.RefillMockData.mockRefillHistoryLogEntries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun RefillHistoryScreen(
    refillHistoryFlow: Flow<PagingData<RefillHistoryLogEntry>>,
    previewData: List<RefillHistoryLogEntry>? = null
) {
    // This screen displays:
    // - headline
    // - list of refill events
    val context = LocalContext.current
    val refillHistoryLogEntries = refillHistoryFlow.collectAsLazyPagingItems()

    // Issue Toast when loading fails
    if (LocalInspectionMode.current) {
        LaunchedEffect(key1 = refillHistoryLogEntries.loadState) {
            if (refillHistoryLogEntries.loadState.refresh is LoadState.Error) {
                val loadState = (refillHistoryLogEntries.loadState.refresh as LoadState.Error)
                val errorMessage = loadState.error.message
                Toast.makeText(context, "API error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.refill_log))

        Scaffold(
            topBar = {},
            bottomBar = {},
            floatingActionButton = {},
            snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() })}
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isRefreshing = if (previewData == null) {
                    refillHistoryLogEntries.loadState.mediator?.refresh is LoadState.Loading ||
                            refillHistoryLogEntries.loadState.refresh is LoadState.Loading
                }
                else {
                    false
                }

                PullToRefreshBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    state = rememberPullToRefreshState(),
                    isRefreshing = isRefreshing,
                    onRefresh = { refillHistoryLogEntries.refresh() },
                    indicator = {
                        // Custom indicator to match the requested size if needed 
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val loadState = refillHistoryLogEntries.loadState

                        when {
                            (loadState.mediator?.refresh is LoadState.Loading || loadState.refresh is LoadState.Loading)
                                    && refillHistoryLogEntries.itemCount == 0
                                    && previewData == null -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center).size(size = 150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                            }
                            loadState.refresh is LoadState.Error
                                    && refillHistoryLogEntries.itemCount == 0
                                    && previewData == null -> {
                                val error = (loadState.refresh as LoadState.Error).error
                                Text(
                                    text = error.localizedMessage ?: "Unknown Error",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            loadState.refresh is LoadState.NotLoading
                                    && refillHistoryLogEntries.itemCount == 0
                                    && previewData == null -> {
                                Text(
                                    text = stringResource(R.string.no_refill_events_found),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            else -> {
                                if (LocalInspectionMode.current) {
                                    // In Preview: Render a simple, static Column instead of LazyColumn
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        previewData?.forEach { refillHistoryLogEntry ->
                                            RefillHistoryLogEntryComposable(
                                                refillHistoryLogEntry
                                            )
                                        }
                                    }
                                }
                                else {
                                    // In App Runtime: Use the performant LazyColumn
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(count = refillHistoryLogEntries.itemCount) { index ->
                                            val refillHistoryLogEntry =
                                                refillHistoryLogEntries[index]
                                            if (refillHistoryLogEntry != null) {
                                                RefillHistoryLogEntryComposable(
                                                    refillHistoryLogEntry
                                                )
                                            }
                                        }
                                        item {
                                            if (loadState.append is LoadState.Loading || loadState.mediator?.append is LoadState.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.fillMaxWidth()
                                                        .padding(16.dp).size(size = 50.dp),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun RefillHistoryScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory,
        screenContent = {
            RefillHistoryScreen(
                refillHistoryFlow = flowOf(PagingData.from(
                    mockRefillHistoryLogEntries,
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true)
                    )
                )),
                previewData = mockRefillHistoryLogEntries
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
internal fun RefillHistoryScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory,
        screenContent = {
            RefillHistoryScreen(
                refillHistoryFlow = flowOf(PagingData.from(
                    emptyList(),
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true)
                    )
                )),
                previewData = mockRefillHistoryLogEntries
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun RefillHistoryScreenLoadingDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory,
        screenContent = {
            RefillHistoryScreen(
                refillHistoryFlow = flowOf(PagingData.from(emptyList(), LoadStates(
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
internal fun RefillHistoryScreenLoadingLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory,
        screenContent = {
            RefillHistoryScreen(
                refillHistoryFlow = flowOf(PagingData.from(emptyList(), LoadStates(
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