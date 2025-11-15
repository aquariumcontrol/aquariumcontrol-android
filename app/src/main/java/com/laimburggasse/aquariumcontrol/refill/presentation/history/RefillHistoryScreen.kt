package com.laimburggasse.aquariumcontrol.refill.presentation.history

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import java.sql.Timestamp

@Composable
fun RefillHistoryScreen(
    showFullWeek: Boolean,
    refillHistoryUiState: RefillHistoryUiState,
    onRefillHistoryEvent: (RefillHistoryUiEvent) -> Unit
) {
    // This screen displays
    // - headline
    // - subhead line
    // - a list of refill events downloaded from the server using a GET request.

    LaunchedEffect(key1 = Unit) {
        onRefillHistoryEvent(RefillHistoryUiEvent.SetRange(showFullWeek))
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.refill_log))
        val subHeadlineText: String =
            if (showFullWeek) {
                stringResource(id = R.string.refill_events_last_7d)
            } else {
                stringResource(id = R.string.refill_events_last_24h)
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
                PullToRefreshBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    state = rememberPullToRefreshState(),
                    isRefreshing = refillHistoryUiState is RefillHistoryUiState.Loading,
                    onRefresh = { onRefillHistoryEvent(RefillHistoryUiEvent.Refresh) },
                    indicator = {
                        if (refillHistoryUiState is RefillHistoryUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 150.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when (refillHistoryUiState) {
                            is RefillHistoryUiState.Empty -> {
                                Text(
                                    text = stringResource(R.string.no_refill_events_found),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(weight = 1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            is RefillHistoryUiState.Error -> {
                                Text(
                                    text = stringResource(R.string.error_loading_data) +
                                            refillHistoryUiState.errorMessage,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(weight = 1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            is RefillHistoryUiState.Loading -> {
                                Text(
                                    text = stringResource(R.string.loading_refill_events),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(weight = 1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            is RefillHistoryUiState.Success -> {
                                val refillEvents: List<RefillHistoryLogEntry> =
                                    refillHistoryUiState.refillHistoryLogEntries
                                if (LocalInspectionMode.current) {
                                    // In Preview: Render a simple, static Column instead of LazyColumn
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        refillEvents.forEach { refillEvent ->
                                            RefillEventComposable(refillHistoryLogEntry = refillEvent)
                                        }
                                    }
                                } else {
                                    // In App Runtime: Use the performant LazyColumn
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(items = refillEvents) { refillEvent ->
                                            RefillEventComposable(refillHistoryLogEntry = refillEvent)
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun RefillHistoryScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
        screenContent = {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryUiState = mockRefillHistoryUiStateSuccess,
                onRefillHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
        screenContent = {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryUiState = mockRefillHistoryUiStateError,
                onRefillHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewEmpty() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
        screenContent = {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryUiState = mockRefillHistoryUiStateEmpty,
                onRefillHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

/* deactivated until update of DataFetchResult
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewLoading() {
    RefillHistoryScreen(
        showFullWeek = true,
        refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
            refillHistoryRepository = mockRefillHistoryRepositorySuccess
        ),
    )
}
*/
