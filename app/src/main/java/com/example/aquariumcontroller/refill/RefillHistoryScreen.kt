package com.example.aquariumcontroller.refill

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.MainViewModelFactory
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Timestamp

@Composable
fun RefillHistoryScreen(
    navHostController: NavHostController?,
    showFullWeek: Boolean,
    mockRefillEvents: List<RefillEvent>?
) {
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory
    )

    var refillEvents by remember { mutableStateOf(mockRefillEvents) }

    val url = when {
        showFullWeek -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.REFILL_GET_EVENTS_7D
        else -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.REFILL_GET_EVENTS_24H
    }
    val reader = RefillEventReader(url)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            refillEvents = reader.readRefillEvents()
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
                    MainDropdownMenuScreen(
                        navHostController,
                        ViewNavigationRoutes.refill
                    )
                    val headlineText: String =
                        if (showFullWeek) {
                            stringResource(R.string.RefillEventsLast7Days)
                        } else {
                            stringResource(R.string.RefillEventsLast24h)
                        }
                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (mainViewModel.isControllerLoaded() || mockRefillEvents != null) {
                            if (mainViewModel.getControllerAddress() != null || mockRefillEvents != null) {
                                if (refillEvents == null) {
                                    Text(
                                        text = "Loading refill events...",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                } else if (refillEvents!!.isEmpty()) {
                                    Text(
                                        text = "No refill events found.",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                } else {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(refillEvents!!) { refillEvent ->
                                            Log.d(
                                                "RefillHistoryScreen",
                                                "Refill events found: ${refillEvents!!.size}"
                                            )
                                            RefillEventScreen(refillEvent)
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
fun RefillHistoryPreview() {
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

    AquariumControllerTheme {
        RefillHistoryScreen(null, true, mockEvents)
    }
}