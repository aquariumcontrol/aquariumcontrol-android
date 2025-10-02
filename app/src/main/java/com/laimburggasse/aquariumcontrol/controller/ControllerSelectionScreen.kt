package com.laimburggasse.aquariumcontrol.controller

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
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
    val defaultProfileId = mainViewModel?.getDefaultControllerProfileId() ?: -1L
    var isDataLoaded by remember { mutableStateOf(false) } // Flag to track data loading

    // Trigger data loading when the screen is composed
    LaunchedEffect(key1 = Unit) {
        // This will trigger the database read operation in the view model
        onEvent(ControllerEvent.LoadControllerProfiles)
        delay(100) // delay for data loading
        isDataLoaded = true
    }

    // Countdown and navigation logic
    LaunchedEffect(key1 = isDataLoaded) {
        if (isDataLoaded && defaultProfileId > 0 && mainViewModel?.isFirstLoad() != false && state.controllerProfileList.isNotEmpty()) {
            // fine grained progressbar for visualization
            (1..40).forEach { _ ->
                delay(100)
                progress += 0.025f
            }

            // Time has passed without user interaction, navigate to the default profile
            val defaultProfile =
                state.controllerProfileList.firstOrNull { it.id == defaultProfileId }
            if (defaultProfile != null) {
                mainViewModel?.setFirstLoad()
                withContext(Dispatchers.Main) { // Switch to the main thread
                    mainViewModel?.updateSelectedControllerProfile(defaultProfile)
                    navHostController?.navigate(ViewNavigationRoutes.overView)
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

                    // Progress bar at the top to be visualized
                    // - for preview OR:
                    // - after data is loaded AND
                    // - default profile has been selected before AND
                    // - screen is invoked at startup AND
                    // - list of controller profiles is not empty
                    if (mockProgress != null || // Display progress bar if mockProgress is provided
                        (isDataLoaded && defaultProfileId > 0 &&
                                mainViewModel?.isFirstLoad() != false &&
                                state.controllerProfileList.isNotEmpty())
                    ) {
                        Text(text = stringResource(R.string.loading_default_profile))
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
                                    contentDescription = stringResource(R.string.create_new_profile)
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
                            val modifier = Modifier.padding(16.dp).wrapContentSize()

                            if (LocalInspectionMode.current) {
                                Column(modifier) {
                                    val items = state.controllerProfileList
                                    items.forEach { item ->
                                        ControllerProfileComposable(
                                            navHostController = null,
                                            onEvent =  { },
                                            controllerProfile = item,
                                            mainViewModel = null,
                                        )
                                    }
                                }
                            }
                            else {
                                LazyColumn(modifier) {
                                    items(items =state.controllerProfileList) { controllerProfile ->
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=600dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=600dp,orientation=landscape"
)
@Composable
internal fun ControllerSelectionScreenPreview() {
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

