package com.laimburggasse.aquariumcontrol.schedule

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import java.util.Locale

/**
 * A stateless composable for adjusting a time-based schedule.
 * It receives all data to display and emits events for user interactions.
 * It has no knowledge of ViewModels.
 */
@Composable
fun ScheduleAdjustmentComposable(
    modifier: Modifier = Modifier,
    title: String,
    startTime: Float,
    stopTime: Float,
    isLimitationActive: Boolean,
    onStartTimeChanged: (Float) -> Unit,
    onStopTimeChanged: (Float) -> Unit,
    onLimitationActiveChanged: (Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current

    // State for controlling the visibility of the Time Picker Dialogs
    var showStartTimeDialog by remember { mutableStateOf(false) }
    var showStopTimeDialog by remember { mutableStateOf(false) }

    // --- DIALOG COMPOSABLES ---
    // When showStartTimeDialog is true, this dialog will be displayed.
    if (showStartTimeDialog) {
        TimePickerDialog(
            initialHour = getHour(startTime),
            initialMinute = getMinute(startTime),
            onTimeSelected = { hour, minute ->
                onStartTimeChanged(hour + minute / 60f)
                showStartTimeDialog = false // Hide the dialog
            },
            onDismiss = {
                showStartTimeDialog = false // Hide the dialog on dismissal
            }
        )
    }

    // When showStopTimeDialog is true, this dialog will be displayed.
    if (showStopTimeDialog) {
        TimePickerDialog(
            initialHour = getHour(stopTime),
            initialMinute = getMinute(stopTime),
            onTimeSelected = { hour, minute ->
                onStopTimeChanged(hour + minute / 60f)
                showStopTimeDialog = false // Hide the dialog
            },
            onDismiss = {
                showStopTimeDialog = false // Hide the dialog on dismissal
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
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

        val sliderRange = startTime..stopTime

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Start Time Button
                    Column(
                        modifier = Modifier.weight(0.25f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.start_time) + formatTime(startTime),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Button(onClick = { showStartTimeDialog = true }) {
                            Text(stringResource(R.string.select_time))
                        }
                    }
                    // Range Slider
                    Column(modifier = Modifier.weight(0.5f)) {
                        RangeSlider(
                            value = sliderRange,
                            onValueChange = { newRange ->
                                onStartTimeChanged(newRange.start)
                                onStopTimeChanged(newRange.endInclusive)
                            },
                            valueRange = 0f..24f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Stop Time Button
                    Column(
                        modifier = Modifier.weight(0.25f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.stop_time) + formatTime(stopTime),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Button(onClick = { showStopTimeDialog = true }) {
                            Text(stringResource(R.string.select_time))
                        }
                    }
                }
            }
            else -> { // Portrait orientation
                Column {
                    RangeSlider(
                        value = sliderRange,
                        onValueChange = { newRange ->
                            onStartTimeChanged(newRange.start)
                            onStopTimeChanged(newRange.endInclusive)
                        },
                        valueRange = 0f..24f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row {
                    Column {
                        Text(
                            text = stringResource(R.string.start_time) + formatTime(startTime),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Button(onClick = { showStartTimeDialog = true }) {
                            Text(text = stringResource(R.string.select_time))
                        }
                    }
                    Spacer(modifier = Modifier.size(width = 72.dp, height = 10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.stop_time) + formatTime(stopTime),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Button(onClick = { showStopTimeDialog = true }) {
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
                checked = isLimitationActive,
                onCheckedChange = onLimitationActiveChanged,
            )
            Text(
                text = stringResource(R.string.activate_schedule_restriction),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/**
 * A wrapper composable for the Android TimePickerDialog to make it work cleanly with Compose state.
 */
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dialog = remember {
        android.app.TimePickerDialog(
            context,
            { _, hour, minute -> onTimeSelected(hour, minute) },
            initialHour,
            initialMinute,
            true // 24-hour view
        )
    }

    dialog.setOnDismissListener { onDismiss() }

    // Use LaunchedEffect to show the dialog safely within the composition
    LaunchedEffect(Unit) {
        dialog.show()
    }
}

// --- Helper functions for time conversion ---

private fun formatTime(value: Float): String {
    val hours = getHour(value)
    val minutes = getMinute(value)
    return String.format(Locale.US, "%02d:%02d", hours, minutes)
}

private fun getHour(value: Float): Int = value.toInt()
private fun getMinute(value: Float): Int = ((value - getHour(value)) * 60).toInt()


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

@Preview(showBackground = true)
@Composable
internal fun ScheduleAdjustmentComposablePreview() {
    AquariumControllerTheme {
        Surface {
            ScheduleAdjustmentComposable(
                title = "Balling Dosing Schedule",
                startTime = 8.5f, // 08:30
                stopTime = 20.25f, // 20:15
                isLimitationActive = true,
                onStartTimeChanged = {},
                onStopTimeChanged = {},
                onLimitationActiveChanged = {}
            )
        }
    }
}
