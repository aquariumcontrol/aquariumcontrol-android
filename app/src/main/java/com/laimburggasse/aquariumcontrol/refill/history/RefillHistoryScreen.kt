package com.laimburggasse.aquariumcontrol.refill.history

import android.content.res.Configuration
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
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.overview.mockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import java.sql.Timestamp

@Composable
fun RefillHistoryScreen(
    showFullWeek: Boolean,
    mainViewModel: MainViewModel,
    refillHistoryViewModelFactory: RefillHistoryViewModelFactory,
) {
    // This screen displays
    // - headline
    // - subhead line
    // - a list of refill events downloaded from the server using a GET request.

    val refillHistoryViewModel: RefillHistoryViewModel = viewModel(
        factory = refillHistoryViewModelFactory
    )
    
    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()
    
    val refillHistoryUiState: RefillHistoryUiState by refillHistoryViewModel.refillHistoryUiState.collectAsState()

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            refillHistoryViewModel.loadData(showFullWeek)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text = stringResource(R.string.refill_log))
        val subHeadlineText: String =
            if (showFullWeek) {
                stringResource(R.string.refill_events_last_7d)
            } else {
                stringResource(R.string.refill_events_last_24h)
            }
        Text(text = subHeadlineText)

        Scaffold(
            topBar = { },
            bottomBar = { },
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
                    onRefresh = { refillHistoryViewModel.loadData(showFullWeek) },
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
                                    text = "Error loading data: ${(refillHistoryUiState as RefillHistoryUiState.Error).errorMessage}",
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
                                val refillEvents =
                                    (refillHistoryUiState as RefillHistoryUiState.Success).refillHistoryEvents
                                if (LocalInspectionMode.current) {
                                    // In Preview: Render a simple, static Column instead of LazyColumn
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        refillEvents.forEach { refillEvent ->
                                            RefillEventComposable(refillHistoryEvent = refillEvent)
                                        }
                                    }
                                } else {
                                    // In App Runtime: Use the performant LazyColumn
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(items = refillEvents) { refillEvent ->
                                            RefillEventComposable(refillHistoryEvent = refillEvent)
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

val mockRefillHistoryEvents = listOf(
    RefillHistoryEvent(
        timestamp = Timestamp(0L),
        duration = 60.0,
        volume = 10.0
    ),
    RefillHistoryEvent(
        timestamp = Timestamp(1000L),
        duration = 30.0,
        volume = 5.0
    )
)

val mockRefillHistoryRepositorySuccess = MockRefillHistoryRepository(
    mockData = DataFetchResult.Success(data = mockRefillHistoryEvents)
)

val mockRefillHistoryRepositoryEmpty = MockRefillHistoryRepository(
    mockData = DataFetchResult.Success(data = emptyList())
)

val mockRefillHistoryRepositoryError = MockRefillHistoryRepository(
    mockData = DataFetchResult.Error(exception = null, errorMessage = "Error message")
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
        mainViewModel = mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
        screenContent = {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositorySuccess
                ),
                mainViewModel = mockMainViewModel,
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewError() {
    MainScaffold(
        mainViewModel = mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
        screenContent = {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositoryError
                ),
                mainViewModel = mockMainViewModel,
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewEmpty() {
    MainScaffold(
        mainViewModel = _root_ide_package_.com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
        screenContent = {
            RefillHistoryScreen(
                showFullWeek = true,
                refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                    refillHistoryRepository = mockRefillHistoryRepositoryEmpty
                ),
                mainViewModel = mockMainViewModel,
            )
        },
        onNavigate = {}
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
        mainViewModel = mockMainViewModel
    )
}
*/
