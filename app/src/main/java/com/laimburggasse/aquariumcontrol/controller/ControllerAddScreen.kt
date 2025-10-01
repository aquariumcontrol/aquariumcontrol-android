package com.laimburggasse.aquariumcontrol.controller

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

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

    val configuration = LocalConfiguration.current

    LaunchedEffect(key1 = Unit) {
        // Inhibit the countdown and automatic navigation when returning to ControllerSelectionScreen
        mainViewModel?.setFirstLoad()
    }

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
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
internal fun ControllerAddScreenPreview() {
    ControllerAddScreen(state = ControllerState(), onEvent = {})
}