package com.example.aquariumcontroller.controller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerSelectionScreen(
    navHostController: NavHostController? = null,
    state: ControllerState,
    onEvent: (ControllerEvent) -> Unit,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays a list of controller profiles.
    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navHostController?.navigate(ViewNavigationRoutes.controllerAdd)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create New Profile"
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.controllerSelection,
                        mainViewModel = mainViewModel
                    )
                    Text(
                        text = stringResource(id = R.string.ListOfControllers),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .wrapContentSize()
                    ) {
                        items(state.controllerProfileList) { controllerProfile ->
                            ControllerProfileComposable(
                                navHostController,
                                onEvent,
                                controllerProfile,
                                mainViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControllerSelectionScreenPreview() {
    val mockControllerProfile1 = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
        isDefault = true
    )
    val mockControllerProfile2 = ControllerProfile(
        name = "Controller 2",
        address = "servername.external.com",
        port = 80,
        user = "userXYZ",
        password = "passwordXYZ",
        isDefault = false
    )

    val controllerProfileList = listOf(
        mockControllerProfile1, mockControllerProfile2
    )

    val mockControllerState = ControllerState()
    mockControllerState.controllerProfileList = controllerProfileList

    AquariumControllerTheme {
        ControllerSelectionScreen(state = mockControllerState, onEvent = {})
    }
}

