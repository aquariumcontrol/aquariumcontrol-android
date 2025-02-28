package com.example.aquariumcontroller.feed.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.webapi.WebApiUrls
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.webapi.WebApiPostRequest
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.FeedProfileImport
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webapi.webApiPostRequestExecution
import java.sql.Timestamp
import java.util.Calendar
import java.util.Locale

@Composable
fun FeedProfileScheduleScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    mockFeedProfile: FeedProfile? = null,
    mockFeedScheduleEntry: FeedScheduleEntry? = null
) {
    val feedProfile = mainViewModel?.getMySelectedFeedProfile() ?: mockFeedProfile
    val feedScheduleEntry = mainViewModel?.getMySelectedFeedScheduleEntry() ?: mockFeedScheduleEntry

    val feedProfileDisplayName =
        feedScheduleEntry?.profileName ?: (feedProfile?.profileName ?: "ERROR")

    var repeatDaily by remember { mutableStateOf(feedScheduleEntry?.isDaily) }

    val coroutineScope = rememberCoroutineScope()

    val webApiPostRequest = WebApiPostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = "Feed Profile Scheduling",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                )

                // Get the initial date and time from the timestamp
                val initialCalendar = Calendar.getInstance(Locale.getDefault()).apply {
                    timeInMillis = feedScheduleEntry?.timestamp?.time ?: System.currentTimeMillis()
                }

                // Initialize state variables with values from the Calendar
                var selectedYear by remember { mutableIntStateOf(initialCalendar.get(Calendar.YEAR)) }
                var selectedMonth by remember { mutableIntStateOf(initialCalendar.get(Calendar.MONTH)) }
                var selectedDay by remember { mutableIntStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }
                var selectedHour by remember { mutableIntStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
                var selectedMinute by remember { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedYear = year
                        selectedMonth = month
                        selectedDay = dayOfMonth
                    },
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )

                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedHour = hourOfDay
                        selectedMinute = minute
                    },
                    selectedHour,
                    selectedMinute,
                    true // Use 24-hour format
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text(text = "Profile name: $feedProfileDisplayName")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(onClick = { datePickerDialog.show() }) {
                            Text("Select Date")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "$selectedDay.${selectedMonth + 1}.$selectedYear",
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(onClick = { timePickerDialog.show() }) {
                            Text("Select Time")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                selectedHour,
                                selectedMinute
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = repeatDaily == true,
                            onCheckedChange = { repeatDaily = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Repeat Daily"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to confirm and pass the selected date and time
                    Button(
                        onClick = {
                            val selectedCalendar = Calendar.getInstance(Locale.getDefault())
                            selectedCalendar.set(
                                selectedYear,
                                selectedMonth,
                                selectedDay,
                                selectedHour,
                                selectedMinute
                            )
                            feedScheduleEntry?.timestamp = Timestamp(selectedCalendar.timeInMillis)
                            Log.d(
                                "FeedProfileScheduleScreen",
                                "feedScheduleEntry: $feedScheduleEntry"
                            )
                            if (mainViewModel != null) {
                                if (feedScheduleEntry != null) {
                                    Log.d(
                                        "FeedProfileScheduleScreen",
                                        "scheduling feedScheduleEntry: $feedScheduleEntry"
                                    )
                                } else {
                                    Log.d(
                                        "FeedProfileScheduleScreen",
                                        "feedScheduleEntry is null. Using feedProfile: $feedProfile"
                                    )
                                }
                                val apiUrl: String =
                                    if (feedProfile == null) WebApiUrls.FEED_POST_PROFILE_SCHEDULE
                                    else WebApiUrls.FEED_POST_PROFILE_UPDATE_SCHEDULE

                                val optionalGetParams =
                                    feedScheduleEntry?.getApiParameters()
                                        ?: feedProfile?.getFeedProfileSchedulingApiCallParameters(
                                            isDaily = repeatDaily == true,
                                            timestamp = Timestamp(selectedCalendar.timeInMillis)
                                        )

                                if (optionalGetParams != null) {
                                    webApiPostRequestExecution(
                                        mainViewModel = mainViewModel,
                                        coroutineScope = coroutineScope,
                                        webApiPostRequest = webApiPostRequest,
                                        context = context,
                                        optionalGetParams = optionalGetParams,
                                        apiUrl = apiUrl
                                    )
                                }
                            }
                            // Show the Toast after the API call
                            Toast.makeText(
                                context,
                                "Sent request to controller for updating feed schedule",
                                Toast.LENGTH_SHORT
                            ).show()
                            navHostController?.navigate(ViewNavigationRoutes.feed)
                        },
                    ) {
                        Text("Confirm Scheduling")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedSchedulePreview() {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile 1"

    val mockFeedProfile = FeedProfile(mockFeedProfileImport)

    val mockFeedScheduleEntry = FeedScheduleEntryImport(
        timestampString = "2024-02-16 12:00:00",
        profileName = "MockProfile",
        isDaily = 1
    ).transformToFeedScheduleEntry()

    FeedProfileScheduleScreen(
        mockFeedProfile = mockFeedProfile,
        mockFeedScheduleEntry = mockFeedScheduleEntry
    )
}