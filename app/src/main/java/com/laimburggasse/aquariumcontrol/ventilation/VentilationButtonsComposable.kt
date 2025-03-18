package com.laimburggasse.aquariumcontrol.ventilation

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope

@Composable
fun VentilationButtonsComposable(
    ventilationSetValsViewModel: VentilationSetValsViewModel? = null,
    mainViewModel: MainViewModel? = null,
    coroutineScope: CoroutineScope? = null
) {
    // This composable displays two buttons for the Ventilation control.
    // Arrangement of the buttons follows the device's orientation.

    val webApiPostRequest = WebApiPostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    Button(
        onClick = {
            ventilationSetValsViewModel?.updateValues()
            if (mainViewModel != null && coroutineScope != null && ventilationSetValsViewModel != null) {
                webApiPostRequestExecution(
                    mainViewModel = mainViewModel,
                    apiUrl = WebApiUrls.VENTILATION_POST_SET_VALS,
                    coroutineScope = coroutineScope,
                    webApiPostRequest = webApiPostRequest,
                    context = context,
                    optionalGetParams = mapOf(
                        WebApiParams.VENTILATION_SWITCH_ON_TEMPERATURE to ventilationSetValsViewModel.getVentilationSwitchOnTempString(),
                        WebApiParams.VENTILATION_SWITCH_OFF_TEMPERATURE to ventilationSetValsViewModel.getVentilationSwitchOffTempString()
                    ),
                )
                // Show the Toast after the API call
                Toast.makeText(
                    context,
                    context.getString(R.string.sent_request_to_controller_for_updating_ventilation_set_values),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    ) {
        Text(text = stringResource(R.string.update_ventilation_controller_values))
    }
    Spacer(modifier = Modifier.width(10.dp))
    Button(
        onClick = { ventilationSetValsViewModel?.resetValues() }
    ) {
        Text(text = stringResource(R.string.reset_ventilation_controller_values))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun VentilationButtonsComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VentilationButtonsComposable()
            }
        }
    }
}

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
fun VentilationButtonsComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                VentilationButtonsComposable()
            }
        }
    }
}