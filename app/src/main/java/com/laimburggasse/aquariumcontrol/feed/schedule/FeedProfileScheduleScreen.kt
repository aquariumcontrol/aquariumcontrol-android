package com.laimburggasse.aquariumcontrol.feed.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import java.sql.Timestamp
import java.util.Calendar
import java.util.Locale

@Composable
fun FeedProfileScheduleScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    mockFeedProfile: FeedProfile? = null,
    mockFeedScheduleEntry: FeedScheduleEntry? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays:
    // - Headline
    // - Feed profile name
    // - Button to start date picker
    // - Text showing the chosen date
    // - Button to start time picker
    // - Text showing the chosen time
    // - Checkbox to repeat daily
    // - Button to confirm the scheduling of the feed profile

    val feedProfile = mainViewModel.getMySelectedFeedProfile() ?: mockFeedProfile

    val feedScheduleEntryOpt = mainViewModel.getMySelectedFeedScheduleEntry() ?: mockFeedScheduleEntry

    val feedScheduleEntry = feedScheduleEntryOpt ?: FeedScheduleEntry(
            timestamp = Timestamp(System.currentTimeMillis()),
            profileName = feedProfile?.profileName ?: "ERROR",
            isDaily = false
        )

    val feedScheduleViewModel = mainViewModel.getMyFeedScheduleViewModel()

    val feedProfileDisplayName: String = feedProfile?.profileName ?: "ERROR"

    var repeatDaily by remember { mutableStateOf(feedScheduleEntry.isDaily) }

    val context = LocalContext.current // context for web API request and Toasts

    AquariumControllerTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(R.string.feed_scheduling)
                    )

                    // Get the initial date and time from the timestamp
                    val initialCalendar = Calendar.getInstance(Locale.getDefault()).apply {
                        timeInMillis =
                            feedScheduleEntry.timestamp.time
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
                            Text(text = stringResource(id = R.string.profile_name_label) + feedProfileDisplayName)
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
                                Text(stringResource(R.string.select_time))
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
                                checked = repeatDaily,
                                onCheckedChange = { repeatDaily = it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.repeat_daily)
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
                                feedScheduleEntry.timestamp =
                                    Timestamp(selectedCalendar.timeInMillis)
                                feedScheduleViewModel?.addFeedScheduleEntry(
                                    feedScheduleEntry = feedScheduleEntry,
                                    feedProfile = feedProfile,
                                    repeatDaily = repeatDaily,
                                    mainViewModel = mainViewModel,
                                    selectedCalendar = selectedCalendar
                                )
                                // Show the Toast after the API call
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.sent_request_to_controller_for_updating_feed_schedule),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navHostController?.navigate(ViewNavigationRoutes.feed)
                            },
                        ) {
                            Text(stringResource(R.string.confirm_scheduling))
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

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
internal fun FeedProfileScheduleScreenPreview() {
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
        mainViewModel = mockMainViewModel,
        mockFeedProfile = mockFeedProfile,
        mockFeedScheduleEntry = mockFeedScheduleEntry
    )
}