package com.laimburggasse.aquariumcontrol.refill.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.overview.mockMainViewModel
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import java.sql.Timestamp

@Composable
fun RefillHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mainViewModel: MainViewModel,
    refillHistoryViewModel: RefillHistoryViewModelInterface = viewModel<RefillHistoryViewModel>(),
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - subhead line
    // - a list of refill events downloaded from the server using a GET request.
    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()
    val isRefreshing: Boolean by remember { refillHistoryViewModel::isRefreshing }
    val refillHistoryUiState: RefillHistoryUiState by remember { refillHistoryViewModel::refillHistoryUiState }

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            refillHistoryViewModel.loadData(mainViewModel, showFullWeek)
        }
    }

    AquariumControllerTheme(darkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ScreenHeadlineComposable(
                        stringResource(R.string.refill_log)
                    )
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
                        snackbarHost = { SnackbarHost(remember { SnackbarHostState() })}
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PullToRefreshBox(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                                state = rememberPullToRefreshState(),
                                isRefreshing = isRefreshing,
                                onRefresh = { refillHistoryViewModel.loadData(mainViewModel, showFullWeek) },
                                indicator = {
                                    if (isRefreshing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(150.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .verticalScroll(state = rememberScrollState())
                                ) {
                                    when(refillHistoryUiState) {
                                        is RefillHistoryUiState.Empty -> {
                                            Text(
                                                text = stringResource(R.string.no_refill_events_found),
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        is RefillHistoryUiState.Error -> {
                                            Text(
                                                "Error loading data: ${(refillHistoryUiState as RefillHistoryUiState.Error).errorMessage}",
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.weight(1f),
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
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        is RefillHistoryUiState.Success -> {
                                            val refillEvents = (refillHistoryUiState as RefillHistoryUiState.Success).refillHistory
                                            if (LocalInspectionMode.current) {
                                                Column(modifier = Modifier.fillMaxSize()) {
                                                    refillEvents.forEach { refillEvent ->
                                                        RefillEventComposable(refillEvent)
                                                    }
                                                }
                                            }
                                            else {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                    items(refillEvents) { refillEvent ->
                                                        RefillEventComposable(refillEvent)
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
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.refill,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

val mockRefillHistoryUiStateSuccess = RefillHistoryUiState.Success(listOf(
    RefillEvent(
        timestamp = Timestamp(0L),
        duration = 60.0,
        volume = 10.0
    ),
    RefillEvent(
        timestamp = Timestamp(1000L),
        duration = 30.0,
        volume = 5.0
    )
))

val mockRefillHistoryViewModelSuccess = MockRefillHistoryViewModel(mockRefillHistoryUiState = mockRefillHistoryUiStateSuccess)

val mockRefillHistoryUiStateError = RefillHistoryUiState.Error(errorMessage = "Error message")

val mockRefillHistoryViewModelError = MockRefillHistoryViewModel(mockRefillHistoryUiState = mockRefillHistoryUiStateError)

val mockRefillHistoryUiStateLoading = RefillHistoryUiState.Loading

val mockRefillHistoryViewModelLoading = MockRefillHistoryViewModel(mockRefillHistoryUiState = mockRefillHistoryUiStateLoading)

val mockRefillHistoryUiStateEmpty = RefillHistoryUiState.Empty

val mockRefillHistoryViewModelEmpty = MockRefillHistoryViewModel(mockRefillHistoryUiState = mockRefillHistoryUiStateEmpty)

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
    RefillHistoryScreen(
        showFullWeek = true,
        refillHistoryViewModel = mockRefillHistoryViewModelSuccess,
        mainViewModel = mockMainViewModel
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewError() {
    RefillHistoryScreen(
        showFullWeek = true,
        refillHistoryViewModel = mockRefillHistoryViewModelError,
        mainViewModel = mockMainViewModel
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewLoading() {
    RefillHistoryScreen(
        showFullWeek = true,
        refillHistoryViewModel = mockRefillHistoryViewModelLoading,
        mainViewModel = mockMainViewModel
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewEmpty() {
    RefillHistoryScreen(
        showFullWeek = true,
        refillHistoryViewModel = mockRefillHistoryViewModelEmpty,
        mainViewModel = mockMainViewModel
    )
}