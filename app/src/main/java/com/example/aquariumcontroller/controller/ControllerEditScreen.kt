package com.example.aquariumcontroller.controller

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.lib_composables.NumericIntOutlinedTextFieldComposable
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerEditScreen(
    navHostController: NavHostController? = null,
    state: ControllerState,
    onEvent: (ControllerEvent) -> Unit,
    mainViewModel: MainViewModel? = null,
    mockIsDefaultProfile: Boolean? = false
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

    var previousDefaultProfileId: Long? = -1

    val initialStateIsDefaultProfile: Boolean = if (mainViewModel != null) {
        (state.controllerProfileId == mainViewModel.getDefaultControllerProfileId())
    } else {
        mockIsDefaultProfile ?: false
    }

    var isDefaultProfile by remember { mutableStateOf(initialStateIsDefaultProfile) }

    // This screen displays a form for editing an existing controller profile.
    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.edit_profile)
                    )

                    OutlinedTextField(
                        value = state.controllerProfileName,
                        onValueChange = {
                            Log.d(this::class.simpleName, "Setting controller profile name: $it")
                            onEvent(ControllerEvent.SetControllerProfileName(it))
                        },
                        label = { Text(stringResource(id = R.string.profile_name)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = state.controllerProfileAddress,
                        onValueChange = {
                            onEvent(ControllerEvent.SetControllerAddress(it))
                        },
                        label = { Text(stringResource(id = R.string.hostname)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    NumericIntOutlinedTextFieldComposable(
                        value = state.controllerProfilePort,
                        onValueChange = {
                            onEvent(ControllerEvent.SetControllerPort(it))
                        },
                        label = stringResource(id = R.string.port_number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    OutlinedTextField(
                        value = state.controllerProfileUser,
                        onValueChange = {
                            onEvent(ControllerEvent.SetControllerUser(it))
                        },
                        label = { Text(stringResource(id = R.string.user_name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    OutlinedTextField(
                        value = state.controllerProfilePassword,
                        onValueChange = {
                            onEvent(ControllerEvent.SetControllerPassword(it))
                        },
                        label = { Text(stringResource(id = R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(), // Hide password
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isDefaultProfile,
                            onCheckedChange = {
                                if (it) {
                                    previousDefaultProfileId =
                                        mainViewModel?.getDefaultControllerProfileId()
                                    mainViewModel?.setDefaultControllerProfileId(state.controllerProfileId)
                                    isDefaultProfile = true
                                } else {
                                    mainViewModel?.setDefaultControllerProfileId(
                                        previousDefaultProfileId ?: -1
                                    )
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
                            onEvent(ControllerEvent.UpdateControllerProfile)
                            navHostController?.navigate(ViewNavigationRoutes.controllerSelection)
                        },
                        enabled = state.controllerProfileName.isNotBlank() && state.validControllerAddress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(stringResource(id = R.string.update_controller_profile))
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.info,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ControllerEditViewPreview() {

    val mockControllerState = ControllerState(
        controllerProfileName = "Controller",
        controllerProfileAddress = "192.168.1.1",
        controllerProfilePort = 80,
        controllerProfileUser = "user",
        controllerProfilePassword = "password",
    )

    ControllerEditScreen(state = mockControllerState, onEvent = {}, mockIsDefaultProfile = true)
}