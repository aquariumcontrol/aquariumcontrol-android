package com.laimburggasse.aquariumcontrol.balling.presentation.history

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
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable

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

    LaunchedEffect(key1 = Unit) {
        onEvent(BallingHistoryUiEvent.SetRange(showFullWeek))
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

        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPullToRefreshState(),
            isRefreshing = ballingHistoryUiState == BallingHistoryUiState.Loading,
            contentAlignment = Alignment.Center,
            onRefresh = { onEvent(BallingHistoryUiEvent.Refresh) },
            indicator = {
                if (ballingHistoryUiState == BallingHistoryUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 150.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
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
                        isRefreshing = ballingHistoryUiState is BallingHistoryUiState.Loading,
                        onRefresh = { onEvent(BallingHistoryUiEvent.Refresh) },
                        indicator = {
                            if (ballingHistoryUiState is BallingHistoryUiState.Loading) {
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
                            when (ballingHistoryUiState) {
                                is BallingHistoryUiState.Empty -> {
                                    Text(
                                        text = stringResource(id = R.string.no_balling_events_found),
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.weight(weight = 1f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                is BallingHistoryUiState.Error -> {
                                    Text(
                                        text = stringResource(R.string.error_loading_balling_data) +
                                                ballingHistoryUiState.errorMessage,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.weight(weight = 1f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                is BallingHistoryUiState.Loading -> {
                                    Text(
                                        text = stringResource(id = R.string.loading_balling_dosing_events),
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.weight(weight = 1f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                is BallingHistoryUiState.Success -> {
                                    val ballingHistoryDosingEvents: List<BallingHistoryLogEntry> =
                                        ballingHistoryUiState.ballingHistoryLogEntries
                                    if (LocalInspectionMode.current) {
                                        // In Preview: Render a simple, static Column instead of LazyColumn
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            ballingHistoryDosingEvents.forEach { ballingHistoryLogEntry ->
                                                BallingHistoryEventComposable(ballingHistoryLogEntry = ballingHistoryLogEntry)
                                            }
                                        }
                                    } else {
                                        // In App Runtime: Use the performant LazyColumn
                                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                                            items(items = ballingHistoryDosingEvents) { ballingHistoryLogEntry ->
                                                BallingHistoryEventComposable(ballingHistoryLogEntry = ballingHistoryLogEntry)
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


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingHistoryScreenPreviewSuccess() {
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
        onNavigate = {}
    )

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryScreenPreviewEmpty() {
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
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryScreenPreviewError() {
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
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryScreenPreviewLoading() {
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
        darkTheme = isSystemInDarkTheme()
    )
}