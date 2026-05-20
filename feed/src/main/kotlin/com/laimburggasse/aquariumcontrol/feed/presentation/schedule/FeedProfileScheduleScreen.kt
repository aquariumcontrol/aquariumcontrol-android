/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.presentation.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedProfileForScheduleScreen
import java.sql.Timestamp
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@Composable
fun FeedProfileScheduleScreen(
    feedProfile: FeedProfile?,
    isNewScheduleEntry: Boolean,
    millisSinceEpoch: Long,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit,
    onFeedScheduleEntryUiEvent: (FeedScheduleEntryUiEvent) -> Unit,
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

    val messageSentFeedScheduleUpdateRequest = stringResource(
        R.string.sent_request_to_controller_for_updating_feed_schedule
    )

    var repeatDaily: Boolean by remember { mutableStateOf(value = false) }

    val timestamp: Timestamp = if (isNewScheduleEntry) {
        // use current time if no millisSinceEpoch is provided
        Timestamp(System.currentTimeMillis())
    }
    else {
        // use millisSinceEpoch if provided (from existing feed schedule entry)
        Timestamp(millisSinceEpoch)
    }
    val feedScheduleEntry = FeedScheduleEntry(
        timestamp,
        profileId = feedProfile?.profileID ?: -1,
        profileName = feedProfile?.profileName ?: "Non existing profile",
        isDaily = repeatDaily
    )
    // Get the initial date and time from the timestamp
    val initialCalendar = Calendar.getInstance(LocalLocale.current.platformLocale).apply {
        timeInMillis = feedScheduleEntry.timestamp.time
    }
    // Initialize state variables with values from the Calendar
    var selectedYear: Int by remember {mutableIntStateOf(value = initialCalendar.get(Calendar.YEAR))}
    var selectedMonth: Int by remember {mutableIntStateOf(value = initialCalendar.get(Calendar.MONTH))}
    var selectedDay: Int by remember {mutableIntStateOf(value = initialCalendar.get(Calendar.DAY_OF_MONTH))}
    var selectedHour: Int by remember {mutableIntStateOf(value = initialCalendar.get(Calendar.HOUR_OF_DAY))}
    var selectedMinute: Int by remember {mutableIntStateOf(value = initialCalendar.get(Calendar.MINUTE))}

    val context: Context = LocalContext.current // context for Toasts

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
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text = stringResource(R.string.feed_scheduling))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (feedProfile != null) {
                Row {
                    Text(text = stringResource(id = R.string.profile_name_label) + feedProfile.profileName)
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                Row {
                    Button(onClick = { datePickerDialog.show() }) {
                        Text(text = stringResource(id = R.string.select_date))
                    }
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = "$selectedDay.${selectedMonth + 1}.$selectedYear",
                        modifier = Modifier.padding(all = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                Row {
                    Button(onClick = { timePickerDialog.show() }) {
                        Text(text = stringResource(R.string.select_time))
                    }
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = String.format(
                            LocalLocale.current.platformLocale,
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                        ),
                        modifier = Modifier.padding(all = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = repeatDaily,
                        onCheckedChange = { repeatDaily = it }
                    )
                    Spacer(Modifier.width(width = 8.dp))
                    Text(
                        text = stringResource(R.string.repeat_daily)
                    )
                }
                Spacer(modifier = Modifier.height(height = 16.dp))

                // Button to confirm and pass the selected date and time
                Button(
                    modifier = Modifier.testTag("confirm_scheduling_button"),
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
                        onFeedScheduleEntryUiEvent(
                            FeedScheduleEntryUiEvent.UpdateFeedScheduleEntry(
                                feedProfile,
                                feedScheduleEntry,
                                selectedCalendar,
                                millisSinceEpoch,
                                isNewEntry = isNewScheduleEntry
                            )
                        )
                        // Show the Toast after the API call
                        Toast.makeText(
                            context,
                            messageSentFeedScheduleUpdateRequest,
                            Toast.LENGTH_SHORT
                        ).show()
                        onNavigateFromFeed(FeedNavigationEvent.ToFeedMain, null, null, null)
                    },
                ) {
                    Text(text = stringResource(R.string.confirm_scheduling))
                }
            }
            else {
                Text(text = "Internal error: Could not identify feed profile.")
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedProfileScheduleScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedProfileScheduleScreen(
                feedProfile = mockFeedProfileForScheduleScreen,
                isNewScheduleEntry = false,
                millisSinceEpoch = 0,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedScheduleEntryUiEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedProfileScheduleScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedProfileScheduleScreen(
                feedProfile = mockFeedProfileForScheduleScreen,
                isNewScheduleEntry = false,
                millisSinceEpoch = 0,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedScheduleEntryUiEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedProfileScheduleScreenErrorDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedProfileScheduleScreen(
                feedProfile = null,
                isNewScheduleEntry = false,
                millisSinceEpoch = 0,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedScheduleEntryUiEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FeedProfileScheduleScreenErrorLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedProfileScheduleScreen(
                feedProfile = null,
                isNewScheduleEntry = false,
                millisSinceEpoch = 0,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedScheduleEntryUiEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}