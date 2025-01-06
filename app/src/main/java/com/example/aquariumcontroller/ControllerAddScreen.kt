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
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerAddScreen(navHostController: NavHostController?) {
    val stringResourceServerProfileNameExample: String = stringResource(id = R.string.ServerProfileNameExample)
    val stringResourceServerAddressExample: String = stringResource(id = R.string.ServerAddressExample)
    val stringResourceServerPortExample: String = stringResource(id = R.string.ServerPortExample)
    val stringResourceServerUserExample: String = stringResource(id = R.string.ServerUserExample)
    val stringResourceServerPasswordExample: String = ""

    var profileName by remember { mutableStateOf(stringResourceServerProfileNameExample) }
    var serverAddress by remember { mutableStateOf(stringResourceServerAddressExample) }
    var serverPort by remember { mutableStateOf(stringResourceServerPortExample) }
    var serverUser by remember { mutableStateOf(stringResourceServerUserExample) }
    var serverPassword by remember { mutableStateOf(stringResourceServerPasswordExample) }
    var isDefaultProfile by remember { mutableStateOf(false) }

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
            value = profileName,
            onValueChange = { profileName = it },
            label = { Text(stringResource(id = R.string.ControllerProfileName)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = serverAddress,
            onValueChange = { serverAddress = it },
            label = { Text(stringResource(id = R.string.ControllerAddress)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text(stringResource(id = R.string.ControllerAddress)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = serverUser,
            onValueChange = { serverUser = it },
            label = { Text(stringResource(id = R.string.ControllerUser)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = serverPassword,
            onValueChange = { serverPassword = it },
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
                checked = isDefaultProfile,
                onCheckedChange = { isDefaultProfile = it }
            )
            Text(
                text = stringResource(id = R.string.IsDefaultServerProfile),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Button(
            onClick = {
                // @todo: create entry in local repository
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
        ControllerAddScreen(null)
    }
}