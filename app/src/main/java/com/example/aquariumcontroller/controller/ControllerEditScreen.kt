package com.example.aquariumcontroller.controller

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.NumericIntOutlinedTextFieldComposable
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerEditScreen(
    navHostController: NavHostController? = null,
    state: ControllerState,
    onEvent: (ControllerEvent) -> Unit
) {
    // This screen displays a form for editing an existing controller profile.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.EditServerProfileFragmentTitle),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.controllerProfileName,
            onValueChange = {
                Log.d(this::class.simpleName, "Setting controller profile name: $it")
                onEvent(ControllerEvent.SetControllerProfileName(it))
            },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.controllerProfileAddress,
            onValueChange = {
                onEvent(ControllerEvent.SetControllerAddress(it))
            },
            label = { Text(stringResource(id = R.string.ControllerAddress)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        NumericIntOutlinedTextFieldComposable(
            value = state.controllerProfilePort,
            onValueChange = {
                onEvent(ControllerEvent.SetControllerPort(it))
            },
            label = stringResource(id = R.string.ControllerPort),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = state.controllerProfileUser,
            onValueChange = {
                onEvent(ControllerEvent.SetControllerUser(it))
            },
            label = { Text(stringResource(id = R.string.ControllerUser)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = state.controllerProfilePassword,
            onValueChange = {
                onEvent(ControllerEvent.SetControllerPassword(it))
            },
            label = { Text(stringResource(id = R.string.ControllerPasswordTitle)) },
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
                checked = state.controllerProfileIsDefault,
                onCheckedChange = { onEvent(ControllerEvent.SetControllerIsDefault(it)) }
            )
            Text(
                text = stringResource(id = R.string.IsDefaultServerProfile),
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
            Text(stringResource(id = R.string.UpdateControllerProfileButtonText))
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
        controllerProfileIsDefault = true
    )

    AquariumControllerTheme {
        ControllerEditScreen(state = mockControllerState, onEvent = {})
    }
}