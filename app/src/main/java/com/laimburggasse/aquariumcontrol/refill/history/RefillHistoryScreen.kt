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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp

@Composable
fun RefillHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mockRefillHistoryUiState: RefillHistoryUiState? = null,
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - subhead line
    // - a list of refill events downloaded from the server using a GET request.
    // There is user interaction to modify data, hence no view model is used.

    var refillHistoryUiState by remember { mutableStateOf(mockRefillHistoryUiState ?: RefillHistoryUiState.Loading) }

    var isRefreshing: Boolean by remember { mutableStateOf(value = true) }

    val coroutineScope = rememberCoroutineScope()

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    fun loadData() {
        coroutineScope.launch {
            if (mainViewModel != null) {
                fetchRefillEvents(
                    mainViewModel,
                    showFullWeek
                ) { newRefillUiState ->
                    refillHistoryUiState = newRefillUiState
                }
            }
        }
    }

    // Use a LaunchedEffect to fetch data on initial composition
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            loadData()
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
                                onRefresh = {
                                    // This lambda will be called when the user pulls to refresh
                                    isRefreshing = true
                                    // Perform the refresh operation here (e.g., fetch new data)
                                    coroutineScope.launch {
                                        if (isControllerAddressValid) {
                                            loadData()
                                            delay(timeMillis = 500)
                                        }
                                        isRefreshing = false // Reset the refresh state
                                    }
                                },
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

private suspend fun fetchRefillEvents(
    mainViewModel: MainViewModel,
    showFullWeek: Boolean,
    updateRefillUiState: (RefillHistoryUiState) -> Unit,
) {
    updateRefillUiState(RefillHistoryUiState.Loading)
    withContext(Dispatchers.IO) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_24H
        }
        val reader = RefillEventReader(url)
        when (val result = reader.readRefillEvents()) {
            is DataFetchResult.Success -> {
                val refillEvents = result.data
                withContext(Dispatchers.Main) {
                    if (refillEvents.isEmpty()) {
                        updateRefillUiState(RefillHistoryUiState.Empty)
                    }
                    else {
                        updateRefillUiState(RefillHistoryUiState.Success(refillEvents))
                    }
                }
            }
            is DataFetchResult.Error -> {
                withContext(context = Dispatchers.Main) {
                    updateRefillUiState(RefillHistoryUiState.Error(result.errorMessage))
                }
            }
        }
    }
}

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
    val mockRefillHistoryUiState = RefillHistoryUiState.Success(listOf(
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

    RefillHistoryScreen(showFullWeek = true, mockRefillHistoryUiState = mockRefillHistoryUiState)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewError() {
    val mockRefillHistoryUiState = RefillHistoryUiState.Error("Error message")

    RefillHistoryScreen(showFullWeek = true, mockRefillHistoryUiState = mockRefillHistoryUiState)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewLoading() {
    val mockRefillHistoryUiState = RefillHistoryUiState.Loading

    RefillHistoryScreen(showFullWeek = true, mockRefillHistoryUiState = mockRefillHistoryUiState)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillHistoryScreenPreviewEmpty() {
    val mockRefillHistoryUiState = RefillHistoryUiState.Empty

    RefillHistoryScreen(showFullWeek = true, mockRefillHistoryUiState = mockRefillHistoryUiState)
}