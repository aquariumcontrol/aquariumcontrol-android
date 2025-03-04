package com.example.aquariumcontroller.controller

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun ControllerAddScreen(
    navHostController: NavHostController? = null,
    state: ControllerState,
    onEvent: (ControllerEvent) -> Unit,
    mainViewModel: MainViewModel? = null,
) {
    // This screen displays a form for adding a new controller profile:
    // - editable text field for the controller profile name
    // - editable text field for the controller address
    // - editable text field for the controller port
    // - editable text field for the controller user
    // - editable text field for the controller password
    // - button to add the controller profile and navigate to the controller selection screen
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
                        text =  stringResource(id = R.string.add_profile)
                    )

                    OutlinedTextField(
                        value = state.controllerProfileName,
                        onValueChange = {
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

                    Button(
                        onClick = {
                            onEvent(ControllerEvent.AddControllerProfile)
                            navHostController?.navigate(ViewNavigationRoutes.controllerSelection)
                        },
                        enabled = state.controllerProfileName.isNotBlank() && state.validControllerAddress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(stringResource(id = R.string.create_profile))
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
fun ControllerAddViewPreview() {
    ControllerAddScreen(state = ControllerState(), onEvent = {})
}