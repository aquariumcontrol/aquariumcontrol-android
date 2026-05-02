/*
 *  Copyright 2026 Uwe Martin
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

package com.laimburggasse.aquariumcontrol.common.presentation

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : Any> GenericHistoryScreen(
    historyFlow: Flow<PagingData<T>>,
    headline: String,
    noEventsMessage: String,
    entryComposable: @Composable (T) -> Unit,
    previewData: List<T>? = null
) {
    val context = LocalContext.current
    val historyLogEntries = historyFlow.collectAsLazyPagingItems()

    // Issue Toast when loading fails
    if (LocalInspectionMode.current) {
        LaunchedEffect(key1 = historyLogEntries.loadState) {
            if (historyLogEntries.loadState.refresh is LoadState.Error) {
                val loadState = (historyLogEntries.loadState.refresh as LoadState.Error)
                val errorMessage = loadState.error.message
                Toast.makeText(context, "API error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeadlineComposable(text = headline)

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
                    historyLogEntries.loadState.mediator?.refresh is LoadState.Loading ||
                            historyLogEntries.loadState.refresh is LoadState.Loading
                }
                else {
                    false
                }

                PullToRefreshBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    state = rememberPullToRefreshState(),
                    isRefreshing = isRefreshing,
                    onRefresh = { historyLogEntries.refresh() },
                    indicator = {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val loadState = historyLogEntries.loadState
                        
                        when {
                            (loadState.mediator?.refresh is LoadState.Loading || loadState.refresh is LoadState.Loading)
                                    && historyLogEntries.itemCount == 0
                                    && previewData == null -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center).size(size = 150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                            }
                            loadState.refresh is LoadState.Error
                                    && historyLogEntries.itemCount == 0
                                    && previewData == null -> {
                                val error = (loadState.refresh as LoadState.Error).error
                                Text(
                                    text = error.localizedMessage ?: "Unknown Error",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            loadState.refresh is LoadState.NotLoading
                                    && historyLogEntries.itemCount == 0
                                    && previewData == null -> {
                                Text(
                                    text = noEventsMessage,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            else -> {
                                if (LocalInspectionMode.current) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        previewData?.forEach { entry ->
                                            entryComposable(entry)
                                        }
                                    }
                                }
                                else {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(count = historyLogEntries.itemCount) { index ->
                                            val entry = historyLogEntries[index]
                                            if (entry != null) {
                                                entryComposable(entry)
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
