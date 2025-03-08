package com.example.aquariumcontroller.balling.edit

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.webapi.WebApiParams
import com.example.aquariumcontroller.webapi.WebApiUrls
import com.example.aquariumcontroller.maindropdownmenu.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.webapi.WebApiPostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.balling.BallingProfile
import com.example.aquariumcontroller.balling.BallingProfileSummaryComposable
import com.example.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webapi.webApiPostRequestExecution

@Composable
fun BallingProfileEditScreen(
    navHostController: NavHostController?,
    ballingProfile: BallingProfile,
    mainViewModel: MainViewModel? = null,
) {
    // This screen displays:
    // - Headline
    // - Edit field for the dosing volume of the selected balling profile
    // - Slider to adjust the dosing volume
    // - Text to display the current dosing volume
    // - Button to confirm the new dosing volume

    val ballingProfileEditViewModel: BallingProfileEditViewModel = viewModel(
        factory = BallingProfileEditViewModelFactory(ballingProfile)
    )

    val webApiPostRequest = WebApiPostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val coroutineScope = rememberCoroutineScope()

    val sliderPositionNewDosingVolume by ballingProfileEditViewModel.ballingNewDosingVolume.observeAsState(
        initial = ballingProfile.volume
    )

    val configuration = LocalConfiguration.current

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.balling_dosing)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    ) {
                        BallingProfileSummaryComposable(ballingProfile = ballingProfile)
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    BallingProfileEditTextFieldSliderComposable(
                                        sliderPositionNewDosingVolume = sliderPositionNewDosingVolume,
                                        ballingProfileEditViewModel = ballingProfileEditViewModel
                                    )
                                }
                            }

                            else -> {
                                BallingProfileEditTextFieldSliderComposable(
                                    sliderPositionNewDosingVolume = sliderPositionNewDosingVolume,
                                    ballingProfileEditViewModel = ballingProfileEditViewModel
                                )
                            }
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            onClick = {
                                ballingProfileEditViewModel.onConfirmDosing()
                                if (mainViewModel != null) {
                                    webApiPostRequestExecution(
                                        mainViewModel = mainViewModel,
                                        apiUrl = WebApiUrls.BALLING_POST_SET_VALS,
                                        coroutineScope = coroutineScope,
                                        webApiPostRequest = webApiPostRequest,
                                        context = context,
                                        optionalGetParams = mapOf(
                                            WebApiParams.PUMP_ID to ballingProfile.pumpId
                                                .toString(),
                                            WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
                                        )
                                    )
                                    // Show the Toast after the API call
                                    Toast.makeText(
                                        context,
                                        "Sent request to controller for updating dosing volume",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            },
                        ) {
                            Text(
                                text = stringResource(id = R.string.update_dosing_value),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.balling,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

val mockBallingProfile = BallingProfile(
    pumpId = 1,
    volume = 1.5,
    label = "MockDosingUnit 1",
)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
fun BallingProfileEditPreviewScreen() {
    BallingProfileEditScreen(
        navHostController = null,
        ballingProfile = mockBallingProfile,
    )
}