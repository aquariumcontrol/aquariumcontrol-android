package com.laimburggasse.aquariumcontrol.feed.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.MockFeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import java.sql.Timestamp
import java.util.Calendar
import java.util.Locale

@Composable
fun FeedProfileScheduleScreen(
    mainViewModel: MainViewModel,
    feedProfileId: Int,
    feedScheduleViewModelFactory: FeedScheduleViewModelFactory,
    feedProfilesViewModelFactory: FeedProfilesViewModelFactory,
    isNewScheduleEntry: Boolean,
    millisSinceEpoch: Long,
    darkTheme: Boolean? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit
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

    val feedScheduleViewModel: FeedScheduleViewModel = viewModel(factory = feedScheduleViewModelFactory)
    val feedProfilesViewModel: FeedProfilesViewModel = viewModel(factory = feedProfilesViewModelFactory)

    val feedProfile: FeedProfile? = feedProfilesViewModel.findProfileById(profileID = feedProfileId)

    var repeatDaily: Boolean by remember { mutableStateOf(value = false) }

    val feedScheduleEntryUiState: FeedScheduleEntryUiState = if (feedProfile == null) {
        FeedScheduleEntryUiState.Error(errorMessage = "Feed profile with ID $feedProfileId not found")
    }
    else {
        // use current time if no millisSinceEpoch is provided
        val timestamp = if (isNewScheduleEntry) {
            Timestamp(System.currentTimeMillis())
        }
        else {
            Timestamp(millisSinceEpoch)
        }
        FeedScheduleEntryUiState.Success(
            feedScheduleEntry = FeedScheduleEntry(
                timestamp,
                profileName = feedProfile.profileName,
                repeatDaily = repeatDaily
            )
        )
    }

    val context: Context = LocalContext.current // context for Toasts

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
                    when (feedScheduleEntryUiState) {
                        is FeedScheduleEntryUiState.Error -> {
                            Text(
                                text = "Error loading data: ${feedScheduleEntryUiState.errorMessage}",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        is FeedScheduleEntryUiState.Success -> {
                            val feedScheduleEntry: FeedScheduleEntry = feedScheduleEntryUiState.feedScheduleEntry

                            // Get the initial date and time from the timestamp
                            val initialCalendar = Calendar.getInstance(Locale.getDefault()).apply {
                                timeInMillis = feedScheduleEntry.timestamp.time
                            }

                            // Initialize state variables with values from the Calendar
                            var selectedYear by remember {
                                mutableIntStateOf(
                                    initialCalendar.get(
                                        Calendar.YEAR
                                    )
                                )
                            }
                            var selectedMonth by remember {
                                mutableIntStateOf(
                                    initialCalendar.get(
                                        Calendar.MONTH
                                    )
                                )
                            }
                            var selectedDay by remember {
                                mutableIntStateOf(
                                    initialCalendar.get(
                                        Calendar.DAY_OF_MONTH
                                    )
                                )
                            }
                            var selectedHour by remember {
                                mutableIntStateOf(
                                    initialCalendar.get(
                                        Calendar.HOUR_OF_DAY
                                    )
                                )
                            }
                            var selectedMinute by remember {
                                mutableIntStateOf(
                                    initialCalendar.get(
                                        Calendar.MINUTE
                                    )
                                )
                            }

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
                                    Text(text = stringResource(id = R.string.profile_name_label) + feedScheduleEntry.profileName)
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
                                        val selectedCalendar =
                                            Calendar.getInstance(Locale.getDefault())
                                        selectedCalendar.set(
                                            selectedYear,
                                            selectedMonth,
                                            selectedDay,
                                            selectedHour,
                                            selectedMinute
                                        )
                                        feedScheduleEntry.timestamp =
                                            Timestamp(selectedCalendar.timeInMillis)
                                        feedScheduleViewModel.addFeedScheduleEntry(
                                            feedProfile = feedProfile!!,
                                            repeatDaily = repeatDaily,
                                            selectedCalendar = selectedCalendar
                                        )
                                        // Show the Toast after the API call
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.sent_request_to_controller_for_updating_feed_schedule),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onNavigateFromFeed(FeedNavigationEvent.ToFeedMain, null, null, null)
                                    },
                                ) {
                                    Text(stringResource(R.string.confirm_scheduling))
                                }
                            }
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel,
                    onNavigate
                )
            }
        }
    }
}

val mockMainViewModel = MockMainViewModel()

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
fun FeedProfileScheduleScreenPreviewSuccess() {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile 1"
    val mockFeedProfile: FeedProfile = FeedProfile.fromFeedProfileImport(mockFeedProfileImport)
    val mockFeedScheduleEntry: FeedScheduleEntry = FeedScheduleEntryImport(
        timestampString = "2024-02-16 12:00:00",
        profileName = "MockProfile",
        isDaily = 1
    ).transformToFeedScheduleEntry()
    val mockFeedScheduleEntries = listOf(mockFeedScheduleEntry)
    val mockFeedProfiles: List<FeedProfile> = listOf(mockFeedProfile)
    val mockFeedScheduleRepository = MockFeedScheduleRepository(
        mockFeedScheduleEntries = DataFetchResult.Success(mockFeedScheduleEntries)
    )
    val mockFeedProfilesRepository = MockFeedProfilesRepository(
        mockFeedProfiles = DataFetchResult.Success(mockFeedProfiles)
    )
    val mockFeedScheduleViewModelFactory = FeedScheduleViewModelFactory(mockFeedScheduleRepository, mockFeedProfilesRepository)
    val mockFeedProfilesViewModelFactory = FeedProfilesViewModelFactory(mockFeedProfilesRepository)

    FeedProfileScheduleScreen(
        mainViewModel = mockMainViewModel,
        feedProfileId = 1,
        feedScheduleViewModelFactory = mockFeedScheduleViewModelFactory,
        feedProfilesViewModelFactory = mockFeedProfilesViewModelFactory,
        isNewScheduleEntry = false,
        millisSinceEpoch = 0,
        onNavigate = {},
        onNavigateFromFeed = {event, id, isNew, millis -> }
    )
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileScheduleScreenPreviewError() {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile 1"
    val mockFeedProfile: FeedProfile = FeedProfile.fromFeedProfileImport(mockFeedProfileImport)
    val mockFeedScheduleEntry: FeedScheduleEntry = FeedScheduleEntryImport(
        timestampString = "2024-02-16 12:00:00",
        profileName = "MockProfile",
        isDaily = 1
    ).transformToFeedScheduleEntry()
    val mockFeedScheduleEntries = listOf(mockFeedScheduleEntry)
    val mockFeedProfiles: List<FeedProfile> = listOf(mockFeedProfile)
    val mockFeedScheduleRepository = MockFeedScheduleRepository(
        mockFeedScheduleEntries = DataFetchResult.Success(mockFeedScheduleEntries)
    )
    val mockFeedProfilesRepository = MockFeedProfilesRepository(
        mockFeedProfiles = DataFetchResult.Success(mockFeedProfiles)
    )
    val mockFeedScheduleViewModelFactory = FeedScheduleViewModelFactory(mockFeedScheduleRepository, mockFeedProfilesRepository)
    val mockFeedProfilesViewModelFactory = FeedProfilesViewModelFactory(mockFeedProfilesRepository)
    FeedProfileScheduleScreen(
        mainViewModel = mockMainViewModel,
        feedProfileId = 1,
        feedScheduleViewModelFactory = mockFeedScheduleViewModelFactory,
        feedProfilesViewModelFactory = mockFeedProfilesViewModelFactory,
        isNewScheduleEntry = false,
        millisSinceEpoch = 0,
        onNavigate = {},
        onNavigateFromFeed = {event, id, isNew, millis -> }
    )
}