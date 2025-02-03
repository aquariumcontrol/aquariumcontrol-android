package com.example.aquariumcontroller.balling.edit

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiParams
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.balling.BallingProfile
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest

@Composable
fun BallingProfileEditScreen(
    navHostController: NavHostController?,
    ballingProfile: BallingProfile,
    mainViewModel: MainViewModel? = null,
) {
    // This screen will allow the user to edit the Balling dosing profile.
    val ballingProfileEditViewModel: BallingProfileEditViewModel = viewModel(
        factory = BallingProfileEditViewModelFactory(ballingProfile)
    )

    val postRequest = PostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val coroutineScope = rememberCoroutineScope()

    val sliderPositionNewDosingVolume by ballingProfileEditViewModel.ballingNewDosingVolume.observeAsState(
        initial = ballingProfile.volume
    )
    val ballingConfirmedDosingVolume by ballingProfileEditViewModel.ballingConfirmedDosingVolume.observeAsState(
        initial = ballingProfile.volume
    )
    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.balling,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = stringResource(id = R.string.BallingMineralDosingControl),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface
                            )
                        )
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "Balling dosing profile:",
                            fontSize = 12.sp
                        )
                        Text(
                            text = (ballingProfile.label),
                            fontSize = 12.sp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pump ID: ${
                                ballingProfile.pumpId
                            }",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Dosing volume: ${
                                "%.2f".format(
                                    ballingConfirmedDosingVolume
                                )
                            } ml",
                            fontSize = 12.sp
                        )
                    }
                }
                NumericDoubleOutlinedTextFieldComposable(
                    value = sliderPositionNewDosingVolume,
                    onValueChange = { newStringValue ->
                        if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                            val newNumericValue = newStringValue.toDouble()
                            ballingProfileEditViewModel.onVolumeChange(newNumericValue)
                        }
                    },
                    label = "",
                    modifier = Modifier
                        .padding(top = 16.dp)
                )
                Slider(value = sliderPositionNewDosingVolume.toFloat(),
                    modifier = Modifier.padding(16.dp),
                    onValueChange = {
                        ballingProfileEditViewModel.onVolumeChange(it.toDouble())
                    },
                    valueRange = BallingProfileEditSliderRange.RANGE
                )
                Text(
                    text = "New Dosing volume: ${"%.2f".format(sliderPositionNewDosingVolume)} ml",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        ballingProfileEditViewModel.onConfirmDosing()
                        if (mainViewModel != null) {
                            webApiPostRequest(
                                mainViewModel = mainViewModel,
                                apiUrl = ApiUrls.BALLING_POST_SET_VALS,
                                coroutineScope = coroutineScope,
                                postRequest = postRequest,
                                context = context,
                                optionalGetParams = mapOf(
                                    ApiParams.PUMP_ID to ballingProfile.pumpId
                                        .toString(),
                                    ApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
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
                        text = stringResource(id = R.string.UpdateDosingValue),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BallingProfileEditPreviewScreen() {
    val mockBallingProfile = BallingProfile(
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1",
    )

    AquariumControllerTheme {
        BallingProfileEditScreen(
            navHostController = null,
            ballingProfile = mockBallingProfile,
        )
    }
}