package com.example.aquariumcontroller.controller

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ControllerSelectionScreen(
    navHostController: NavHostController? = null,
    state: ControllerState,
    onEvent: (ControllerEvent) -> Unit,
    mainViewModel: MainViewModel? = null,
    mockProgress: Float? = null
) {
    // This screen displays:
    // - headline
    // - list of controller profiles
    // - floating action button to add new controller profile

    var progress by remember { mutableFloatStateOf(0f) }
    var userInteracted by remember { mutableStateOf(false) }
    val defaultProfileId = mainViewModel?.getDefaultControllerProfileId() ?: -1L
    var isDataLoaded by remember { mutableStateOf(false) } // Flag to track data loading

    // Trigger data loading when the screen is composed
    LaunchedEffect(key1 = Unit) {
        // This will trigger the database read operation in your ViewModel
        // Make sure your ViewModel updates the 'state.controllerProfileList' when the data is loaded
        onEvent(ControllerEvent.LoadControllerProfiles)
        delay(100) // delay for data loading
        isDataLoaded = true
    }

    // Countdown and navigation logic
    LaunchedEffect(key1 = isDataLoaded) {
        if (isDataLoaded && defaultProfileId > 0 && !userInteracted && mainViewModel?.isFirstLoad() != false) {
            // fine grained progressbar for visualization
            for (i in 1..40) {
                delay(100)
                progress += 0.025f
            }
            if (!userInteracted) {
                val defaultProfile =
                    state.controllerProfileList.firstOrNull { it.id == defaultProfileId }
                if (defaultProfile != null) {
                    Log.d(
                        "ControllerSelectionScreen",
                        "Default profile found: ${defaultProfile.name}"
                    )
                    mainViewModel?.setFirstLoad()
                    withContext(Dispatchers.Main) { // Switch to the main thread
                        mainViewModel?.updateSelectedControllerProfile(defaultProfile)
                        navHostController?.navigate(ViewNavigationRoutes.overView)
                    }
                }
            }
        }
    }

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenHeadlineComposable(text = stringResource(id = R.string.list_of_controllers))

                    // Progress bar at the top
                    if (mockProgress != null || (isDataLoaded && !userInteracted && mainViewModel?.isFirstLoad() != false)) {
                        // Only show if
                        // - in preview
                        // - data is loaded and no user interaction
                        Text(text = "Loading default profile...")
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        )
                    }

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
                            LazyColumn(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .wrapContentSize()
                            ) {
                                items(state.controllerProfileList) { controllerProfile ->
                                    ControllerProfileComposable(
                                        navHostController = navHostController,
                                        onEvent = onEvent,
                                        controllerProfile = controllerProfile,
                                        mainViewModel = mainViewModel,
                                    )
                                }
                            }
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.controllerSelection,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ControllerSelectionScreenPreview() {
    val mockControllerProfile1 = ControllerProfile(
        id = 1,
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
    )
    val mockControllerProfile2 = ControllerProfile(
        id = 2,
        name = "Controller 2",
        address = "servername.external.com",
        port = 80,
        user = "userXYZ",
        password = "passwordXYZ",
    )

    val controllerProfileList = listOf(
        mockControllerProfile1, mockControllerProfile2
    )

    val mockControllerState = ControllerState()
    mockControllerState.controllerProfileList = controllerProfileList

    ControllerSelectionScreen(
        state = mockControllerState,
        onEvent = {},
        mockProgress = 0.3f
    )
}

