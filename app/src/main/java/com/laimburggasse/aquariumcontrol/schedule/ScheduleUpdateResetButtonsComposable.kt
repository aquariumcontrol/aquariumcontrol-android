package com.laimburggasse.aquariumcontrol.schedule

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun ScheduleUpdateResetButtonsComposable(
    ballingScheduleEntryViewModel: ScheduleEntryViewModel? = null,
    refillScheduleEntryViewModel: ScheduleEntryViewModel? = null,
    ventilationScheduleEntryViewModel: ScheduleEntryViewModel? = null,
    heatingScheduleEntryViewModel: ScheduleEntryViewModel? = null,
    scheduleEntries: ScheduleEntries,
    scheduleViewModel: ScheduleViewModelInterface,
    mainViewModel: MainViewModel
) {
    val context: Context = LocalContext.current // context for Toast

    Button(
        onClick = {
            if (ballingScheduleEntryViewModel != null &&
                refillScheduleEntryViewModel != null &&
                ventilationScheduleEntryViewModel != null &&
                heatingScheduleEntryViewModel != null
            ) {

                ballingScheduleEntryViewModel.updateValues()
                refillScheduleEntryViewModel.updateValues()
                ventilationScheduleEntryViewModel.updateValues()
                heatingScheduleEntryViewModel.updateValues()

                scheduleEntries.updateFromViewModels(
                    ballingScheduleEntryViewModel = ballingScheduleEntryViewModel,
                    refillScheduleEntryViewModel = refillScheduleEntryViewModel,
                    ventilationScheduleEntryViewModel = ventilationScheduleEntryViewModel,
                    heatingScheduleEntryViewModel = heatingScheduleEntryViewModel
                )
                scheduleViewModel.updateSchedule(
                    mainViewModel = mainViewModel,
                    scheduleEntries = scheduleEntries
                )
                Toast.makeText(
                    context,
                    context.getString(R.string.sent_request_to_controller_for_updating_schedule_set_values),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    ) {
        Text(text = stringResource(R.string.update_actuator_schedules))
    }
    Spacer(modifier = Modifier.width(10.dp))
    Button(
        onClick = {
            ballingScheduleEntryViewModel?.resetValues()
            refillScheduleEntryViewModel?.resetValues()
            ventilationScheduleEntryViewModel?.resetValues()
            heatingScheduleEntryViewModel?.resetValues()
        }
    ) {
        Text(text = "Reset actuator schedules")
    }
}
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun ScheduleUpdateResetButtonsComposablePreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ScheduleUpdateResetButtonsComposable(
                    mainViewModel = mockMainViewModel,
                    scheduleViewModel = mockScheduleViewModelSuccess,
                    scheduleEntries = mockScheduleEntries,
                )
            }
        }
    }
}
