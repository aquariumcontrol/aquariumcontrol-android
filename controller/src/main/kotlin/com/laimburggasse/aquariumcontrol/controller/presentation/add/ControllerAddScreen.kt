/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.controller.presentation.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.R
import com.laimburggasse.aquariumcontrol.controller.domain.isValidPortNumber
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditHostnamePortnumberComposable
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUsernamePasswordComposable

@Composable
fun ControllerAddScreen(
    state: ControllerAddEditUiState,
    onEvent: (ControllerAddEditUiEvent) -> Unit,
    onAddEvent: (ControllerAddEditUiEvent) -> Unit
) {
    // This screen displays a form for adding a new controller profile:
    // - editable text field for the controller profile name
    // - editable text field for the controller address
    // - editable text field for the controller port
    // - editable text field for the controller user
    // - editable text field for the controller password
    // - button to add the controller profile and navigate to the controller selection screen

    val configuration: Configuration = LocalConfiguration.current

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp).verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text =  stringResource(id = R.string.add_profile))

        OutlinedTextField(
            value = state.controllerProfileName,
            onValueChange = { onEvent(ControllerAddEditUiEvent.SetControllerProfileName(controllerProfileName = it)) },
            label = { Text(text = stringResource(id = R.string.profile_name)) },
            modifier = Modifier.fillMaxWidth(),
        )

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                    ControllerAddEditHostnamePortnumberComposable(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier.weight(weight = 0.5f)
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                    ControllerAddEditUsernamePasswordComposable(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier.weight(weight = 0.5f)
                    )
                }
            }
            else -> {
                ControllerAddEditHostnamePortnumberComposable(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
                ControllerAddEditUsernamePasswordComposable(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = { onAddEvent(ControllerAddEditUiEvent.AddControllerProfile) },
            enabled = state.controllerProfileName.isNotBlank() &&
                      state.validControllerAddress &&
                      (state.controllerProfilePort.toIntOrNull()?.let { isValidPortNumber(it) } ?: false),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.create_profile))
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=480dp,orientation=landscape"
)
@Composable
internal fun ControllerAddScreenDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.controllerAdd,
        screenContent = {
            ControllerAddScreen(
                state = ControllerAddEditUiState(),
                onEvent = {},
                onAddEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=480dp,orientation=landscape"
)
@Composable
internal fun ControllerAddScreenLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.controllerAdd,
        screenContent = {
            ControllerAddScreen(
                state = ControllerAddEditUiState(),
                onEvent = {},
                onAddEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}