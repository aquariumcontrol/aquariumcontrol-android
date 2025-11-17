package com.laimburggasse.aquariumcontrol.controller.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.controller.R
import com.laimburggasse.aquariumcontrol.common.presentation.NumericIntOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun ControllerAddEditHostnamePortnumberComposable(
    onEvent: (ControllerAddEditUiEvent) -> Unit,
    modifier: Modifier? = Modifier,
    state: ControllerAddEditUiState
) {
    // This composable displays text fields for host name and port number.
    // The arrangement of the fields follows the device orientation.

    OutlinedTextField(
        value = state.controllerProfileAddress,
        onValueChange = {
            onEvent(ControllerAddEditUiEvent.SetControllerAddress(controllerAddress = it))
        },
        label = { Text(text = stringResource(id = R.string.hostname)) },
        modifier = modifier?.then(Modifier.padding(top = 16.dp)) ?: Modifier
    )
    Spacer(modifier = Modifier.width(width = 10.dp))
    NumericIntOutlinedTextFieldComposable(
        value = state.controllerProfilePort,
        onValueChange = {
            onEvent(ControllerAddEditUiEvent.SetControllerPort(controllerPort = it))
        },
        label = stringResource(id = R.string.port_number),
        modifier = modifier?.then(other = Modifier.padding(top = 16.dp)) ?: Modifier
    )
}

val mockControllerState = ControllerAddEditUiState(
    controllerProfileAddress = "mock.hostname.com",
    controllerProfilePort = 80,
)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun ControllerAddEditHostnamePortnumberComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface {
            Column {
                ControllerAddEditHostnamePortnumberComposable(
                    onEvent = {},
                    modifier = Modifier.fillMaxWidth(),
                    state = mockControllerState
                )
            }
        }
    }
}

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
internal fun ControllerAddEditHostnamePortnumberComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                ControllerAddEditHostnamePortnumberComposable(
                    onEvent = {},
                    modifier = Modifier.weight(weight = 0.5f),
                    state = mockControllerState
                )
            }
        }
    }
}
