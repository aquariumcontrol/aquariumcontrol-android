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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingHistoryUiStateEmpty
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingHistoryUiStateError
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingHistoryUiStateLoading
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingHistoryUiStateSuccess
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import kotlinx.coroutines.flow.flowOf

@Composable
fun BallingHistoryScreen(
    showFullWeek: Boolean,
    ballingHistoryUiState: BallingHistoryUiState,
    onEvent: (BallingHistoryUiEvent) -> Unit,
) {
    // This screen displays:
    // - headline
    // - sub headline (last 7 days or last 24 hours)
    // - list of balling events

    val context = LocalContext.current

    val ballingHistoryLogEntries: LazyPagingItems<BallingHistoryLogEntry>? =
        if (ballingHistoryUiState is BallingHistoryUiState.Success) {
            remember(ballingHistoryUiState.ballingHistoryLogEntries) {
                flowOf(ballingHistoryUiState.ballingHistoryLogEntries)
            }.collectAsLazyPagingItems()
        } else {
            null
        }

    LaunchedEffect(key1 = ballingHistoryLogEntries?.loadState) {
        if (ballingHistoryLogEntries?.loadState?.refresh is LoadState.Error) {
            val loadState = (ballingHistoryLogEntries.loadState.refresh as LoadState.Error)
            val errorMessage = loadState.error.message
            Toast.makeText(context, "API error: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.balling_log))

        val subHeadlineText: String =
            if (showFullWeek) {
                stringResource(R.string.balling_events_last_7d)
            } else {
                stringResource(R.string.balling_events_last_24h)
            }
        Text(text = subHeadlineText)

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
                val isRefreshing = ballingHistoryLogEntries?.loadState?.refresh is LoadState.Loading ||
                        ballingHistoryUiState is BallingHistoryUiState.Loading

                PullToRefreshBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    state = rememberPullToRefreshState(),
                    isRefreshing = isRefreshing,
                    onRefresh = { onEvent(BallingHistoryUiEvent.Refresh) },
                    indicator = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = 150.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (ballingHistoryUiState is BallingHistoryUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center).size(size = 150.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (ballingHistoryUiState is BallingHistoryUiState.Error) {
                            Text(
                                text = ballingHistoryUiState.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else if (ballingHistoryUiState is BallingHistoryUiState.Empty) {
                            Text(
                                text = stringResource(R.string.no_balling_events_found),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else if (ballingHistoryLogEntries != null) {
                            // In App Runtime: Use the performant LazyColumn
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(count = ballingHistoryLogEntries.itemCount) { index ->
                                    val ballingHistoryLogEntry = ballingHistoryLogEntries[index]
                                    if (ballingHistoryLogEntry != null) {
                                        BallingHistoryLogEntryComposable(ballingHistoryLogEntry)
                                    }
                                }
                                item {
                                    if (ballingHistoryLogEntries.loadState.append is LoadState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp).size(size = 50.dp),
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
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateSuccess,
                onEvent = {}
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
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateSuccess,
                onEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenEmptyDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateEmpty,
                onEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryScreenEmptyLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateEmpty,
                onEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenErrorDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateError,
                onEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryScreenErrorLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateError,
                onEvent = {}
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
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateLoading,
                onEvent = {}
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
        navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
        screenContent = {
            BallingHistoryScreen(
                showFullWeek = false,
                ballingHistoryUiState = mockBallingHistoryUiStateLoading,
                onEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}