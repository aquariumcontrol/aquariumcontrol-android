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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun ControllerAddEditUsernamePasswordComposable(
    onEvent: (ControllerAddEditUiEvent) -> Unit,
    modifier: Modifier? = Modifier,
    state: ControllerAddEditUiState? = null,
) {
    // This composable displays text fields for user name and password.
    // The arrangement of the fields follows the device orientation.

    OutlinedTextField(
        value = state?.controllerProfileUser ?: "mock_user",
        onValueChange = {
            onEvent(ControllerAddEditUiEvent.SetControllerUser(controllerUser = it))
        },
        label = { Text(text = stringResource(id = R.string.user_name)) },
        modifier = modifier?.then(other = Modifier.padding(top = 16.dp)) ?: Modifier
    )
    Spacer(modifier = Modifier.width(width = 10.dp))
    OutlinedTextField(
        value = state?.controllerProfilePassword ?: "mock password",
        onValueChange = {
            onEvent(ControllerAddEditUiEvent.SetControllerPassword(controllerPassword = it))
        },
        label = { Text(text = stringResource(id = R.string.password)) },
        visualTransformation = PasswordVisualTransformation(), // Hide password
        modifier = modifier?.then(other = Modifier.padding(top = 16.dp)) ?: Modifier
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun ControllerAddEditUsernamePasswordComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface {
            Column {
                ControllerAddEditUsernamePasswordComposable(
                    onEvent = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun ControllerAddEditUsernamePasswordComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                ControllerAddEditUsernamePasswordComposable(
                    onEvent = {},
                    modifier = Modifier.weight(weight = 0.5f)
                )
            }
        }
    }
}
