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
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BallingHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mockBallingHistoryDosingEvents: List<BallingHistoryDosingEvent>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays:
    // - headline
    // - sub headline (last 7 days or last 24 hours)
    // - list of balling events

    // No user input is expected. There is no need to have a view model.

    var ballingEvents by remember { mutableStateOf(mockBallingHistoryDosingEvents) }
    var isRefreshing by remember {
        mutableStateOf(
            mockBallingHistoryDosingEvents?.isNotEmpty() ?: false
        )
    }

    val isControllerLoaded = mainViewModel?.isControllerLoaded() == true
    val coroutineScope = rememberCoroutineScope()
    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    // Use a LaunchedEffect to fetch data on initial composition
    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            isRefreshing = true
            fetchBallingEvents(
                mainViewModel,
                showFullWeek
            ) { newBallingEvents ->
                ballingEvents = newBallingEvents as List<BallingHistoryDosingEvent>
            } // Pass the setter lambda
            isRefreshing = false
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
                    fetchBallingEvents(
                        mainViewModel!!, showFullWeek
                    ) { newBallingEvents ->
                        ballingEvents = newBallingEvents as List<BallingHistoryDosingEvent>
                    } // Pass the setter lambda
                    delay(500)
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

                    Scaffold{ innerPadding ->
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
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (isControllerLoaded || mockBallingHistoryDosingEvents != null) {
                                        if (isControllerAddressValid || mockBallingHistoryDosingEvents != null) {
                                            if (ballingEvents == null) {
                                                Text(
                                                    text = stringResource(R.string.loading_balling_dosing_events),
                                                    style = TextStyle(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            } else if (ballingEvents!!.isEmpty()) {
                                                Text(
                                                    text = stringResource(R.string.no_balling_events_found),
                                                    style = TextStyle(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                )
                                            } else {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                    items(ballingEvents!!) { ballingEvent ->
                                                        BallingHistoryEventComposable(ballingEvent)
                                                    }
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = stringResource(id = R.string.invalid_controller_address),
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(id = R.string.controller_not_loaded),
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
                    navigationRouteSource = ViewNavigationRoutes.balling,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

private suspend fun fetchBallingEvents(
    mainViewModel: MainViewModel,
    showFullWeek: Boolean,
    updateBallingEvents: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_EVENTS_24H
        }
        val reader = BallingHistoryEventReader(url)
        val ballingEvents = reader.readBallingEvents()
        if (ballingEvents != null) {
            updateBallingEvents(ballingEvents) // Call the setter lambda
        }
    }
}

val ballingHistoryScreenMockData = listOf(
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
internal fun BallingHistoryScreenPreview() {
    BallingHistoryScreen(
        showFullWeek = false,
        mockBallingHistoryDosingEvents = ballingHistoryScreenMockData
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingHistoryScreenEmptyPreview() {
    BallingHistoryScreen(
        showFullWeek = false,
        mockBallingHistoryDosingEvents = emptyList()
    )
}
