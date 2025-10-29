package com.laimburggasse.aquariumcontrol.controller.edit

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditEvent
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditHostnamePortnumberComposable
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditState
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditUsernamePasswordComposable
import com.laimburggasse.aquariumcontrol.controller.common.ControllerNavigationEvent
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable

@Composable
fun ControllerEditScreen(
    state: ControllerAddEditState,
    onEvent: (ControllerAddEditEvent) -> Unit,
    isDefaultProfileInitialValue: Boolean,
    onNavigateFromController: (ControllerNavigationEvent, Long?) -> Unit
) {
    // This screen allows editing of a controller profile and selecting if the profile shall be
    // the default profile.
    // The screen displays the following edit fields:
    // - controller profile name
    // - controller address
    // - controller port
    // - controller user
    // - controller password
    //
    // Furthermore, the screen displays a switch for selecting if the profile shall be the default.
    // Last, the screen features a button to confirm the changes.

    var isDefaultProfile: Boolean by remember { mutableStateOf(isDefaultProfileInitialValue) }

    val configuration: Configuration = LocalConfiguration.current

    // This screen displays a form for editing an existing controller profile.
    Column(
        modifier = Modifier.fillMaxSize().padding(all = 8.dp).verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.edit_profile))

        OutlinedTextField(
            value = state.controllerProfileName,
            onValueChange = {
                onEvent(ControllerAddEditEvent.SetControllerProfileName(controllerProfileName = it))
            },
            label = { Text(text = stringResource(id = R.string.profile_name)) },
            modifier = Modifier.fillMaxWidth(),
        )

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                ) {
                    ControllerAddEditHostnamePortnumberComposable(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier.weight(0.5f)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                ) {
                    ControllerAddEditUsernamePasswordComposable(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier.weight(0.5f)
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

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isDefaultProfile,
                onCheckedChange = {
                    if (it) {
                        onEvent(ControllerAddEditEvent.SetDefaultControllerId(controllerProfileId = state.controllerProfileId))
                        isDefaultProfile = true
                    } else {
                        onEvent(ControllerAddEditEvent.UnsetDefaultControllerId)
                        isDefaultProfile = false
                    }
                }
            )
            Text(
                text = stringResource(id = R.string.app_shall_use_as_default),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Button(
            onClick = {
                onEvent(ControllerAddEditEvent.UpdateControllerProfile)
                onNavigateFromController(ControllerNavigationEvent.ToControllerMain, null)
            },
            enabled = state.controllerProfileName.isNotBlank() && state.validControllerAddress,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.update_controller_profile))
        }
    }
}

val mockControllerEditState = ControllerAddEditState(
    controllerProfileName = "Controller",
    controllerProfileAddress = "192.168.1.1",
    controllerProfilePort = 80,
    controllerProfileUser = "user",
    controllerProfilePassword = "password",
)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=480dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=480dp,orientation=landscape"
)
@Composable
internal fun ControllerEditScreenPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.controllerEdit,
        screenContent = {
            ControllerEditScreen(
                state = mockControllerEditState,
                onEvent = {},
                isDefaultProfileInitialValue = true,
                onNavigateFromController = {event, id -> }
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}