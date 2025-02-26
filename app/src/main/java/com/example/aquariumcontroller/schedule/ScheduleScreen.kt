package com.example.aquariumcontroller.schedule

import android.app.TimePickerDialog
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleScreen(
    navHostController: NavHostController? = null,
    mockScheduleSetVals: ScheduleSetVals? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays for each of (Balling, Refill, schedule, Heating)
    // - a range slider for setting the start and stop time

    val scheduleInitialSetVals = mockScheduleSetVals ?: ScheduleSetVals(0F, 24F, false)

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    val coroutineScope = rememberCoroutineScope()

    val postRequest = PostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val scheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(scheduleInitialSetVals)
    )

    // Use derived state for slider position
    val sliderRange by scheduleSetValsViewModel.sliderRange.observeAsState(
        initial =
        scheduleInitialSetVals.ballingStartTime..
                scheduleInitialSetVals.ballingStopTime
    )

    // Use a separate mutable state for the text field values
    val lowerBoundValue by scheduleSetValsViewModel.lowerBoundValue.observeAsState(
        initial = scheduleInitialSetVals.ballingStartTime
    )
    val upperBoundValue by scheduleSetValsViewModel.upperBoundValue.observeAsState(
        scheduleInitialSetVals.ballingStopTime
    )

    // Get the initial date and time from the timestamp
    val initialCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = System.currentTimeMillis()
        // todo: replace with time read from API
    }

    var selectedHourStart by remember { mutableIntStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinuteStart by remember { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }
    var selectedHourStop by remember { mutableIntStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinuteStop by remember { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchscheduleSetVals(
                mainViewModel,
            ) { newScheduleSetVals ->
                val newScheduleSetValsCasted = newScheduleSetVals as ScheduleSetVals
                Log.d("scheduleScreen", "New schedule set vals: $newScheduleSetValsCasted")
                scheduleSetValsViewModel.setValues(newScheduleSetValsCasted)
            }
        }
    }

    val timePickerDialogStart = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedHourStart = hourOfDay
            selectedMinuteStart = minute
        },
        selectedHourStart,
        selectedMinuteStart,
        true // Use 24-hour format
    )

    val timePickerDialogStop = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedHourStop = hourOfDay
            selectedMinuteStop = minute
        },
        selectedHourStop,
        selectedMinuteStop,
        true // Use 24-hour format
    )

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .padding(innerPadding)
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.schedule,
                        mainViewModel = mainViewModel
                    )
                    Text(
                        text = stringResource(R.string.actuatorSchedule),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            scheduleSetValsViewModel.updateValues()
                            if (mainViewModel != null) {
                                webApiPostRequest(
                                    mainViewModel = mainViewModel,
                                    apiUrl = ApiUrls.ACTUATOR_SCHEDULE_POST_UPDATE,
                                    coroutineScope = coroutineScope,
                                    postRequest = postRequest,
                                    context = context,
                                    optionalGetParams = scheduleSetValsViewModel.getApiParams(),
                                )
                                // Show the Toast after the API call
                                Toast.makeText(
                                    context,
                                    "Sent request to controller for updating schedule set values",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(text = "Update actuator schedules")
                    }
                    Button(
                        onClick = { scheduleSetValsViewModel.resetValues() }
                    ) {
                        Text(text = "Reset actuator schedules")
                    }
                    Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface
                                )
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Balling dosing schedule",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Column {
                            RangeSlider(
                                value = sliderRange,
                                onValueChange = { newValue ->
                                    scheduleSetValsViewModel.onRangeChangedBySlider(
                                        newValue.start,
                                        newValue.endInclusive
                                    )
                                },
                                valueRange = 0f..24f,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        Row {
                            Column {
                                val startTimeString =
                                    scheduleSetValsViewModel.formatTime(lowerBoundValue)
                                Text(
                                    text = "Start time: $startTimeString",
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .size(width = 128.dp, height = 36.dp)
                                )
                                Button(onClick = { timePickerDialogStart.show() }) {
                                    Text("Select Time")
                                }
                            }
                            Spacer(modifier = Modifier.size(width = 72.dp, height = 10.dp))
                            Column {
                                val stopTimeString =
                                    scheduleSetValsViewModel.formatTime(upperBoundValue)
                                Text(
                                    text = "Stop time: $stopTimeString",
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .size(width = 128.dp, height = 36.dp)
                                )
                                Button(onClick = { timePickerDialogStop.show() }) {
                                    Text("Select Time")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun fetchscheduleSetVals(
    mainViewModel: MainViewModel,
    updatescheduleSetVals: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + ApiUrls.ACTUATOR_SCHEDULE_GET_VALS
        /*
        val scheduleSetValsList = scheduleSetValsReader(url).readscheduleSetVals()
        if (!scheduleSetValsList.isNullOrEmpty()) {
            Log.d("scheduleScreen", "Fetched schedule set vals: $scheduleSetValsList")
            withContext(Dispatchers.Main) {
                updatescheduleSetVals(scheduleSetValsList.first())
            }
        }
        else {
            Log.d("scheduleScreen", "No schedule set vals fetched")
        }*/
    }
}

@Preview(showBackground = true, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScheduleScreenPreview() {
    AquariumControllerTheme {
        ScheduleScreen(
            mockScheduleSetVals = ScheduleSetVals(
                ballingStartTime = 2F,
                ballingStopTime = 22F,
                ballingLimitationActive = false,
            )
        )
    }
}