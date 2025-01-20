package com.example.aquariumcontroller.balling

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.MainViewModelFactory
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest

private const val s = "pumpId"

@Composable
fun BallingProfileEditScreen(
    navHostController: NavHostController?,
    mockBallingProfile: BallingProfile? = null,
) {
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory
    )

    val ballingProfileViewModel: BallingProfileViewModel = viewModel(
        factory = BallingProfileViewModelFactory
    )

    val postRequest = PostRequest()

    val context = LocalContext.current

    // Create a CoroutineScope that follows this composable's lifecycle
    val coroutineScope = rememberCoroutineScope()

    val initialDosingVolume = (mockBallingProfile?.volume
        ?: mainViewModel.getSelectedBallingProfileVolume()!!)
    val sliderPositionNewDosingVolume by ballingProfileViewModel.ballingNewDosingVolume.observeAsState(
        initial = initialDosingVolume
    )
    val ballingConfirmedDosingVolume by ballingProfileViewModel.ballingConfirmedDosingVolume.observeAsState(
        initial = initialDosingVolume
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
                MainDropdownMenuScreen(
                    navHostController,
                    ViewNavigationRoutes.balling
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
                        .clickable {
                            Log.d(
                                "BallingProfileScreen",
                                "Clicked on {${mainViewModel.getSelectedBallingProfileLabel()}}"
                            )
                        }
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
                            text = (mockBallingProfile?.label
                                ?: mainViewModel.getSelectedBallingProfileLabel()!!),
                            fontSize = 12.sp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pump ID: ${
                                mockBallingProfile?.pumpId ?: mainViewModel.getSelectedBallingProfilePumpId()
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
                Slider(value = sliderPositionNewDosingVolume.toFloat(),
                    modifier = Modifier.padding(16.dp),
                    onValueChange = {
                        ballingProfileViewModel.onVolumeChange(it.toDouble())
                        ballingProfileViewModel.onVolumeChange(it.toDouble())
                    })
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
                        webApiPostRequest(
                            mainViewModel = mainViewModel,
                            apiUrl = ApiUrls.BALLING_POST_SET_VALS,
                            coroutineScope = coroutineScope,
                            postRequest = postRequest,
                            context = context,
                            optionalGetParams = mapOf(
                                ApiParams.PUMP_ID to mainViewModel.getSelectedBallingProfilePumpId()
                                    .toString(),
                                ApiParams.DOSING_VOLUME to sliderPositionNewDosingVolume.toString()
                            )
                        )
                        mainViewModel.updateSelectedBallingProfileVolume(
                            sliderPositionNewDosingVolume.toFloat()
                        )
                        ballingProfileViewModel.onConfirmDosing()
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
            mockBallingProfile = mockBallingProfile,
            navHostController = null
        )
    }
}