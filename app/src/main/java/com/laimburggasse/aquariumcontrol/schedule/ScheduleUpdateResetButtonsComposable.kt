package com.laimburggasse.aquariumcontrol.schedule

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution

@Composable
fun ScheduleUpdateResetButtonsComposable(
    ballingScheduleSetValsViewModel: ScheduleSetValsViewModel? = null,
    refillScheduleSetValsViewModel: ScheduleSetValsViewModel? = null,
    ventilationScheduleSetValsViewModel: ScheduleSetValsViewModel? = null,
    heatingScheduleSetValsViewModel: ScheduleSetValsViewModel? = null,
    scheduleSetVals: ScheduleSetVals? = null,
    mainViewModel: MainViewModel? = null
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current // context for web API request and Toasts

    Button(
        onClick = {
            if (ballingScheduleSetValsViewModel != null &&
                refillScheduleSetValsViewModel != null &&
                ventilationScheduleSetValsViewModel != null &&
                heatingScheduleSetValsViewModel != null
            ) {

                ballingScheduleSetValsViewModel.updateValues()
                refillScheduleSetValsViewModel.updateValues()
                ventilationScheduleSetValsViewModel.updateValues()
                heatingScheduleSetValsViewModel.updateValues()

                scheduleSetVals?.updateFromViewModels(
                    ballingScheduleSetValsViewModel,
                    refillScheduleSetValsViewModel,
                    ventilationScheduleSetValsViewModel,
                    heatingScheduleSetValsViewModel
                )
                if (mainViewModel != null) {
                    val webApiPostRequest = WebApiPostRequest()
                    webApiPostRequestExecution(
                        mainViewModel = mainViewModel,
                        apiUrl = WebApiUrls.ACTUATOR_SCHEDULE_POST_UPDATE,
                        coroutineScope = coroutineScope,
                        webApiPostRequest = webApiPostRequest,
                        context = context,
                        optionalGetParams = scheduleSetVals?.getApiParams() ?: emptyMap(),
                    )
                    // Show the Toast after the API call
                    Toast.makeText(
                        context,
                        context.getString(R.string.sent_request_to_controller_for_updating_schedule_set_values),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    ) {
        Text(text = stringResource(R.string.update_actuator_schedules))
    }
    Spacer(modifier = Modifier.width(10.dp))
    Button(
        onClick = {
            ballingScheduleSetValsViewModel?.resetValues()
            refillScheduleSetValsViewModel?.resetValues()
            ventilationScheduleSetValsViewModel?.resetValues()
            heatingScheduleSetValsViewModel?.resetValues()
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
                ScheduleUpdateResetButtonsComposable()
            }
        }
    }
}
