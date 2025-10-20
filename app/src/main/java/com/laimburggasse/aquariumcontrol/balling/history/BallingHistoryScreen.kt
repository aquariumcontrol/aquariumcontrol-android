package com.laimburggasse.aquariumcontrol.balling.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.overview.mockMainViewModel
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun BallingHistoryScreen(
    showFullWeek: Boolean,
    mainViewModel: MainViewModel,
    ballingHistoryViewModelFactory: BallingHistoryViewModelFactory,
    darkTheme: Boolean? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    // This screen displays:
    // - headline
    // - sub headline (last 7 days or last 24 hours)
    // - list of balling events

    val ballingHistoryViewModel: BallingHistoryViewModel = viewModel(
        factory = ballingHistoryViewModelFactory
    )

    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()

    val ballingHistoryUiState: BallingHistoryUiState by ballingHistoryViewModel.ballingHistoryUiState.collectAsState()

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            ballingHistoryViewModel.loadData(showFullWeek = showFullWeek)
        }
    }

    AquariumControllerTheme(darkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(R.string.balling_log)
                    )
                    val subHeadlineText: String =
                        if (showFullWeek) {
                            stringResource(R.string.balling_events_last_7d)
                        } else {
                            stringResource(R.string.balling_events_last_24h)
                        }
                    Text(text = subHeadlineText)

                    Scaffold(
                        topBar = { },
                        bottomBar = { },
                        floatingActionButton = {},
                        snackbarHost = { SnackbarHost(remember { SnackbarHostState() })}
                    ){ innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PullToRefreshBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                                state = rememberPullToRefreshState(),
                                isRefreshing = ballingHistoryUiState is BallingHistoryUiState.Loading,
                                onRefresh = { ballingHistoryViewModel.loadData(showFullWeek) },
                                indicator = {
                                    if (ballingHistoryUiState is BallingHistoryUiState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(150.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
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
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        is BallingHistoryUiState.Error -> {
                                            Text(
                                                text = "Error loading data: ${(ballingHistoryUiState as BallingHistoryUiState.Error).errorMessage}",
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.weight(1f),
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
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        is BallingHistoryUiState.Success -> {
                                            val ballingHistoryDosingEvents = (ballingHistoryUiState as BallingHistoryUiState.Success).ballingHistoryDosingEvents
                                            if (LocalInspectionMode.current) {
                                                // In Preview: Render a simple, static Column instead of LazyColumn
                                                Column(modifier = Modifier.fillMaxSize()) {
                                                    ballingHistoryDosingEvents.forEach { ballingHistoryDosingEvent ->
                                                        BallingHistoryEventComposable(ballingHistoryDosingEvent = ballingHistoryDosingEvent)
                                                    }
                                                }
                                            } else {
                                                // In App Runtime: Use the performant LazyColumn
                                                LazyColumn(
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    items(items = ballingHistoryDosingEvents) { ballingHistoryDosingEvent ->
                                                        BallingHistoryEventComposable(ballingHistoryDosingEvent = ballingHistoryDosingEvent)
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
                MainDropdownMenuComposable(
                    navigationRouteSource = ViewNavigationRoutes.balling,
                    mainViewModel = mainViewModel,
                    onNavigate
                )
            }
        }
    }
}

val mockBallingHistoryDosingEvents = listOf(
    BallingHistoryDosingEvent(
        timestamp = "2025-01-18 12:00:00",
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1"
    ),
    BallingHistoryDosingEvent(
        timestamp = "2025-01-18 12:00:00",
        pumpId = 2,
        volume = 2.5,
        label = "MockDosingUnit 2"
    ),
)

val mockBallingHistoryRepositorySuccess = MockBallingHistoryRepository(
    mockData = DataFetchResult.Success(data = mockBallingHistoryDosingEvents)
)

val mockBallingHistoryRepositoryEmpty = MockBallingHistoryRepository(
    mockData = DataFetchResult.Success(data = emptyList())
)

val mockBallingHistoryRepositoryError = MockBallingHistoryRepository(
    mockData = DataFetchResult.Error(exception = null, errorMessage = "Error message")
)

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
    BallingHistoryScreen(
        showFullWeek = false,
        mainViewModel = mockMainViewModel,
        ballingHistoryViewModelFactory = BallingHistoryViewModelFactory(
            ballingHistoryRepository = mockBallingHistoryRepositorySuccess
        ),
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenPreviewEmpty() {
    BallingHistoryScreen(
        showFullWeek = false,
        mainViewModel = mockMainViewModel,
        ballingHistoryViewModelFactory = BallingHistoryViewModelFactory(
            ballingHistoryRepository = mockBallingHistoryRepositoryEmpty
        ),
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenPreviewError() {
    BallingHistoryScreen(
        showFullWeek = false,
        mainViewModel = mockMainViewModel,
        ballingHistoryViewModelFactory = BallingHistoryViewModelFactory(
            ballingHistoryRepository = mockBallingHistoryRepositoryError
        ),
        onNavigate = {}
    )
}

// @todo: reactivate
/* deactivated until after update of DataFetchResult
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenPreviewLoading() {
    BallingHistoryScreen(
        showFullWeek = false,
        mainViewModel = mockMainViewModel,
        ballingHistoryViewModel = mockBallingHistoryViewModelLoading,
    )
}
*/