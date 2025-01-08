package com.example.aquariumcontroller

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
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerAddScreen(
    navHostController: NavHostController?,
    state: ControllerState,
    onEvent: (ControllerEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.AddServerProfileFragmentTitle),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.controllerProfileName,
            onValueChange = {
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

        NumericOutlinedTextField(
            value = state.controllerProfilePort.toString(),
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
                onEvent(ControllerEvent.AddControllerProfile)
                navHostController?.navigate(ViewNavigationRoutes.controllerSelection)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(stringResource(id = R.string.CreateControllerProfileButtonText))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControllerAddViewPreview() {
    AquariumControllerTheme {
        ControllerAddScreen(null, ControllerState(), {})
    }
}