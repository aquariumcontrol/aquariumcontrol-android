package com.example.aquariumcontroller.balling.history

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BallingHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mockBallingHistoryDosingEvents: List<BallingHistoryDosingEvent>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen will only display the different Balling dosing events.
    // Hence, there is no need to have a view model.
    var ballingEvents by remember { mutableStateOf(mockBallingHistoryDosingEvents) }

    val isControllerLoaded = mainViewModel?.isControllerLoaded() ?: false

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    if ((mainViewModel != null) && (isControllerAddressValid)) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.BALLING_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.BALLING_GET_EVENTS_24H
        }
        val reader = BallingHistoryEventReader(url)

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                ballingEvents = reader.readBallingEvents()
            }
        }
    }

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.balling,
                        mainViewModel = mainViewModel
                    )
                    val headlineText: String =
                        if (showFullWeek) {
                            stringResource(R.string.BallingEventsLast7Days)
                        } else {
                            stringResource(R.string.BallingEventsLast24h)
                        }
                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (isControllerLoaded || mockBallingHistoryDosingEvents != null) {
                            if (isControllerAddressValid || mockBallingHistoryDosingEvents != null) {
                                if (ballingEvents == null) {
                                    Text(
                                        text = "Loading Balling dosing events...",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                } else if (ballingEvents!!.isEmpty()) {
                                    Text(
                                        text = "No Balling events found.",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                } else {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(ballingEvents!!) { ballingEvent ->
                                            Log.d(
                                                this::class.simpleName,
                                                "Balling dosing events found: ${ballingEvents!!.size}"
                                            )
                                            BallingHistoryEventComposable(ballingEvent)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Invalid controller address",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "Controller not loaded.",
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
}

@Preview(showBackground = true)
@Composable
fun BallingHistoryPreview() {
    val mockEvents = listOf(
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

    AquariumControllerTheme {
        BallingHistoryScreen(showFullWeek = false, mockBallingHistoryDosingEvents = mockEvents)
    }
}