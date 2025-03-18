package com.laimburggasse.aquariumcontrol.heating

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope

@Composable
fun HeatingButtonsComposable(
    heatingSetValsViewModel: HeatingSetValsViewModel? = null,
    mainViewModel: MainViewModel? = null,
    coroutineScope: CoroutineScope? = null
) {
    // This composable displays two buttons for the heating control.
    // Arrangement of the buttons follows the device's orientation.
    
    val webApiPostRequest = WebApiPostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    Button(
        onClick = {
            heatingSetValsViewModel?.updateValues()
            if (mainViewModel != null && coroutineScope != null && heatingSetValsViewModel != null) {
                webApiPostRequestExecution(
                    mainViewModel = mainViewModel,
                    apiUrl = WebApiUrls.HEATING_POST_SET_VALS,
                    coroutineScope = coroutineScope,
                    webApiPostRequest = webApiPostRequest,
                    context = context,
                    optionalGetParams = mapOf(
                        WebApiParams.HEATING_SWITCH_ON_TEMPERATURE to heatingSetValsViewModel.getHeatingSwitchOnTempString(),
                        WebApiParams.HEATING_SWITCH_OFF_TEMPERATURE to heatingSetValsViewModel.getHeatingSwitchOffTempString()
                    ),
                )
                // Show the Toast after the API call
                Toast.makeText(
                    context,
                    "Sent request to controller for updating heating set values",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    ) {
        Text(text = "Update Heating Controller Values")
    }
    Spacer(modifier = Modifier.width(10.dp))
    Button(
        onClick = { heatingSetValsViewModel?.resetValues() }
    ) {
        Text(text = "Reset Heating Controller Values")
    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingButtonsComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeatingButtonsComposable()
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
fun HeatingButtonsComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                HeatingButtonsComposable()
            }
        }
    }
}