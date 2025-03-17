package com.laimburggasse.aquariumcontroller.refill.history

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontroller.MainViewModel
import com.laimburggasse.aquariumcontroller.R
import com.laimburggasse.aquariumcontroller.ViewNavigationRoutes
import com.laimburggasse.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontroller.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontroller.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefillHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mockRefillEvents: List<RefillEvent>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - headline
    // - subhead line
    // - a list of refill events downloaded from the server using a GET request.
    // There is user interaction to modify data, hence no view model is used.

    var refillEvents by remember { mutableStateOf(mockRefillEvents) }
    var isRefreshing by remember {
        mutableStateOf(
            value = mockRefillEvents?.isNotEmpty() == true
        )
    }
    val isControllerLoaded = mainViewModel?.isControllerLoaded() == true
    val coroutineScope = rememberCoroutineScope()
    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    // Use a LaunchedEffect to fetch data on initial composition
    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchRefillEvents(
                mainViewModel,
                showFullWeek
            ) { newRefillEvents ->
                refillEvents = newRefillEvents as List<RefillEvent>
            } // Pass the setter lambda
        }
    }

    @Suppress("UNCHECKED_CAST")
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            // This lambda will be called when the user pulls to refresh
            isRefreshing = true
            // Perform the refresh operation here (e.g., fetch new data)
            coroutineScope.launch {
                if (isControllerAddressValid) {
                    fetchRefillEvents(
                        mainViewModel!!, showFullWeek
                    ) { newRefillEvents ->
                        refillEvents = newRefillEvents as List<RefillEvent>
                    } // Pass the setter lambda
                    delay(timeMillis = 500)
                    isRefreshing = false // Reset the refresh state
                }
            }
        }
    )

    AquariumControllerTheme {
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
                    Scaffold { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .pullRefresh(pullRefreshState)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(150.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    if (isControllerLoaded || mockRefillEvents != null) {
                                        if (isControllerAddressValid || mockRefillEvents != null) {
                                            if (refillEvents == null) {
                                                Text(
                                                    text = stringResource(R.string.loading_refill_events),
                                                    style = TextStyle(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            } else if (refillEvents!!.isEmpty()) {
                                                Text(
                                                    text = stringResource(R.string.no_refill_events_found),
                                                    style = TextStyle(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            } else {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                    items(refillEvents!!) { refillEvent ->
                                                        RefillEventComposable(refillEvent)
                                                    }
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = stringResource(R.string.invalid_controller_address),
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(R.string.controller_not_loaded),
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
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
    updateRefillEvents: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.REFILL_GET_EVENTS_24H
        }
        val reader = RefillEventReader(url)
        val refillEvents = reader.readRefillEvents()
        if (refillEvents != null) {
            updateRefillEvents(refillEvents) // Call the setter lambda
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
fun RefillHistoryScreenPreview() {
    val mockEvents = listOf(
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
    )

    RefillHistoryScreen(showFullWeek = true, mockRefillEvents = mockEvents)
}