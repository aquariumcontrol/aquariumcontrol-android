package com.laimburggasse.aquariumcontrol.schedule.presentation

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
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun ScheduleUpdateResetButtonsComposable(onScheduleEvent: (ScheduleUiEvent) -> Unit) {
    val context: Context = LocalContext.current // context for Toast

    Button(
        onClick = {
            onScheduleEvent(ScheduleUiEvent.Update)
            Toast.makeText(
                context,
                context.getString(R.string.sent_request_to_controller_for_updating_schedule_set_values),
                Toast.LENGTH_SHORT
            ).show()
        }
    ) {
        Text(text = stringResource(R.string.update_actuator_schedules))
    }
    Spacer(modifier = Modifier.width(width = 10.dp))
    Button(
        onClick = {
            onScheduleEvent(ScheduleUiEvent.Reset)
        }
    ) {
        Text(text = stringResource(id = R.string.reset_actuator_schedules))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun ScheduleUpdateResetButtonsComposablePreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ScheduleUpdateResetButtonsComposable(onScheduleEvent = {})
            }
        }
    }
}
