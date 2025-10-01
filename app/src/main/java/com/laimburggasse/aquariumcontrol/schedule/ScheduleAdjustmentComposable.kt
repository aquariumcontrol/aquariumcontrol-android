package com.laimburggasse.aquariumcontrol.schedule

import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleAdjustmentComposable(
    myViewModel: ScheduleSetValsViewModel,
    title: String
) {
    // This composable displays:
    // - Range slider for setting the start and stop time
    // - Time pickers for setting the start and stop time
    // - Text labels informing the chosen start and stop time
    // - Switch for choosing to enable the schedule restriction

    val configuration = LocalConfiguration.current

    // Use derived state for slider position
    val sliderRange by myViewModel.sliderRange.observeAsState()

    // Use a separate mutable state for the text field values
    val lowerBoundValue by myViewModel.lowerBoundValue.observeAsState()
    val upperBoundValue by myViewModel.upperBoundValue.observeAsState()

    val isScheduleLimitationActive by myViewModel.isLimitationActive.observeAsState()

    // Get the initial date and time from the timestamp
    val initialCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = System.currentTimeMillis()
        // initial value is replaced by view model - loading initiated by upper-level composable
    }

    var selectedHourStart by remember { mutableIntStateOf(
            if (lowerBoundValue != null)
                myViewModel.getHour(lowerBoundValue!!)
            else
                initialCalendar.get(Calendar.HOUR_OF_DAY)
        )
    }
    var selectedMinuteStart by remember { mutableIntStateOf(
            if (lowerBoundValue != null)
                myViewModel.getMinute(lowerBoundValue!!)
            else
                initialCalendar.get(Calendar.MINUTE)
        )
    }
    var selectedHourStop by remember { mutableIntStateOf(
            if (upperBoundValue != null)
                myViewModel.getHour(upperBoundValue!!)
            else
                initialCalendar.get(Calendar.HOUR_OF_DAY)
        )
    }
    var selectedMinuteStop by remember { mutableIntStateOf(
            if (upperBoundValue != null)
                myViewModel.getMinute(upperBoundValue!!)
            else
                initialCalendar.get(Calendar.MINUTE)
        )
    }

    // Update selectedHourStart and selectedMinuteStart when lowerBoundValue changes
    LaunchedEffect(lowerBoundValue) {
        lowerBoundValue?.let {
            selectedHourStart = myViewModel.getHour(it)
            selectedMinuteStart = myViewModel.getMinute(it)
        }
    }

    // Update selectedHourStop and selectedMinuteStop when upperBoundValue changes
    LaunchedEffect(upperBoundValue) {
        upperBoundValue?.let {
            selectedHourStop = myViewModel.getHour(it)
            selectedMinuteStop = myViewModel.getMinute(it)
        }
    }

    val timePickerDialogStart = TimePickerDialog(
        /* context = */ LocalContext.current,
        /* listener = */ { _, hourOfDay, minute ->
            selectedHourStart = hourOfDay
            selectedMinuteStart = minute
            // Update the ViewModel with the new start time
            val newStartTime = hourOfDay + minute / 60f
            myViewModel.onLowerValueChanged(newStartTime)
        },
        /* hourOfDay = */ selectedHourStart,
        /* minute = */ selectedMinuteStart,
        /* is24HourView = */ true // Use 24-hour format
    )

    val timePickerDialogStop = TimePickerDialog(
        /* context = */ LocalContext.current,
        /* listener = */ { _, hourOfDay, minute ->
            selectedHourStop = hourOfDay
            selectedMinuteStop = minute
            // Update the ViewModel with the new stop time
            val newStopTime = hourOfDay + minute / 60f
            myViewModel.onUpperValueChanged(newStopTime)
        },
        /* hourOfDay = */ selectedHourStop,
        /* minute = */ selectedMinuteStop,
        /* is24HourView = */ true // Use 24-hour format
    )

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
            text = title,
            style = MaterialTheme.typography.headlineSmall,
        )
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier.weight(0.25f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val startTimeString =
                            myViewModel.formatTime(lowerBoundValue!!)
                        Text(
                            text = stringResource(R.string.start_time) + startTimeString,
                            modifier = Modifier
                                .padding(top = 16.dp)
                        )
                        Button(onClick = { timePickerDialogStart.show() }) {
                            Text(stringResource(R.string.select_time))
                        }
                    }
                    Column(modifier = Modifier.weight(0.5f)) {
                        RangeSlider(
                            value = sliderRange!!,
                            onValueChange = { newValue ->
                                myViewModel.onRangeChangedBySlider(
                                    newValue.start,
                                    newValue.endInclusive
                                )
                            },
                            valueRange = 0f..24f,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier.weight(0.25f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val stopTimeString =
                            myViewModel.formatTime(upperBoundValue!!)
                        Text(
                            text = stringResource(R.string.stop_time) + stopTimeString,
                            modifier = Modifier
                                .padding(top = 16.dp)
                        )
                        Button(onClick = { timePickerDialogStop.show() }) {
                            Text(stringResource(R.string.select_time))
                        }
                    }
                }

            }

            else -> {
                Column {
                    RangeSlider(
                        value = sliderRange!!,
                        onValueChange = { newValue ->
                            myViewModel.onRangeChangedBySlider(
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
                            myViewModel.formatTime(lowerBoundValue!!)
                        Text(
                            text = stringResource(R.string.start_time) + startTimeString,
                            modifier = Modifier
                                .padding(top = 16.dp)
                        )
                        Button(onClick = { timePickerDialogStart.show() }) {
                            Text(text = stringResource(R.string.select_time))
                        }
                    }
                    Spacer(modifier = Modifier.size(width = 72.dp, height = 10.dp))
                    Column {
                        val stopTimeString =
                            myViewModel.formatTime(upperBoundValue!!)
                        Text(
                            text = stringResource(R.string.stop_time) + stopTimeString,
                            modifier = Modifier
                                .padding(top = 16.dp)
                        )
                        Button(onClick = { timePickerDialogStop.show() }) {
                            Text(text = stringResource(R.string.select_time))
                        }
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Switch(
                checked = isScheduleLimitationActive!!,
                onCheckedChange = {
                    myViewModel.setLimitationActive(it)
                },
            )
            Text(
                text = stringResource(R.string.activate_schedule_restriction),
                modifier = Modifier.padding(start = 8.dp)
            )
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
internal fun ScheduleAdjustmentComposablePreview() {
    val mockScheduleSetVals = ScheduleSetVals(
        ballingStartTime = 2F,
        ballingStopTime = 22F,
        ballingLimitationActive = false,
        refillStartTime = 3F,
        refillStopTime = 21F,
        refillLimitationActive = true,
        ventilationStartTime = 4F,
        ventilationStopTime = 20F,
        ventilationLimitationActive = false,
        heatingStartTime = 5F,
        heatingStopTime = 19F,
        heatingLimitationActive = true,
    )

    // create a view model instance for preview
    val myViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = mockScheduleSetVals.ballingStartTime,
            setValStopTime = mockScheduleSetVals.ballingStopTime,
            setValLimitationActive = mockScheduleSetVals.ballingLimitationActive
        )
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ScheduleAdjustmentComposable(
                myViewModel = myViewModel,
                title = "Balling dosing schedule",
            )
        }
    }
}