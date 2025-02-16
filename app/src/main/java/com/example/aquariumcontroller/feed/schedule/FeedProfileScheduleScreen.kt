package com.example.aquariumcontroller.feed.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.FeedProfileImport
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import java.util.Calendar
import java.util.Locale

@Composable
fun FeedProfileScheduleScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    mockFeedProfile: FeedProfile? = null
) {
    val feedProfile = mainViewModel?.getMySelectedFeedProfile()?: mockFeedProfile
    var repeatDaily by remember { mutableStateOf(false) }

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
                val context = LocalContext.current
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)

                var selectedYear by remember { mutableStateOf(currentYear) }
                var selectedMonth by remember { mutableStateOf(currentMonth) }
                var selectedDay by remember { mutableStateOf(currentDay) }
                var selectedHour by remember { mutableStateOf(currentHour) }
                var selectedMinute by remember { mutableStateOf(currentMinute) }

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
                        Text( text = "Profile name: ${feedProfile?.profileName}")
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
                            text = "$selectedHour:$selectedMinute",
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = repeatDaily,
                            onCheckedChange = { repeatDaily = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Repeat Daily"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to confirm and pass the selected date and time
                    Button(onClick = {
                        val selectedCalendar = Calendar.getInstance(Locale.getDefault())
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                        //onDateTimeSelected(selectedCalendar.timeInMillis)
                        navHostController?.navigate(ViewNavigationRoutes.feed)
                    }) {
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

    AquariumControllerTheme {
        FeedProfileScheduleScreen(mockFeedProfile = mockFeedProfile)
    }
}