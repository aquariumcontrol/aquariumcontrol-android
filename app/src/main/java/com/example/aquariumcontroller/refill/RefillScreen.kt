package com.example.aquariumcontroller.refill

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest

@Composable
fun RefillScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a button for visualizing all refill events of the last 24h
    // - a button for visualizing all refill events of the last 7 days
    // - a button for starting the refill controller
    // - a button for stopping the refill controller
    // - a button for resetting errors of the refill controller

    val postRequest = PostRequest()
    val isControllerLoaded = mainViewModel?.isControllerLoaded() ?: false
    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()
    val context = LocalContext.current // context for web API request and Toasts

    // Create a CoroutineScope that follows this composable's lifecycle
    val coroutineScope = rememberCoroutineScope()

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.refill,
                        mainViewModel = mainViewModel
                    )
                    Text(
                        text = stringResource(id = R.string.RefillControl),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    navHostController?.navigate(ViewNavigationRoutes.refillHistory24h)
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ViewRefillEvents24h),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    navHostController?.navigate(ViewNavigationRoutes.refillHistory7d)
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ViewRefillEvents7d),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    if (isControllerLoaded && isControllerAddressValid) {
                                        webApiPostRequest(
                                            mainViewModel = mainViewModel!!,
                                            apiUrl = ApiUrls.REFILL_POST_CONTROL_START,
                                            coroutineScope = coroutineScope,
                                            postRequest = postRequest,
                                            context = context,
                                        )
                                        // Show the Toast after the API call
                                        Toast.makeText(
                                            context,
                                            "Sent request to controller for starting refill control",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.StartRefillControl),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    if (isControllerLoaded && isControllerAddressValid) {
                                        webApiPostRequest(
                                            mainViewModel = mainViewModel!!,
                                            apiUrl = ApiUrls.REFILL_POST_CONTROL_STOP,
                                            coroutineScope = coroutineScope,
                                            postRequest = postRequest,
                                            context = context,
                                        )
                                        // Show the Toast after the API call
                                        Toast.makeText(
                                            context,
                                            "Sent request to controller for stopping refill control",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.StopRefillControl),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    if (isControllerLoaded && isControllerAddressValid) {
                                        webApiPostRequest(
                                            mainViewModel = mainViewModel!!,
                                            apiUrl = ApiUrls.REFILL_POST_CONTROL_RESET,
                                            coroutineScope = coroutineScope,
                                            postRequest = postRequest,
                                            context = context,
                                        )
                                        // Show the Toast after the API call
                                        Toast.makeText(
                                            context,
                                            "Sent request to controller for resetting errors",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ResetErrors),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RefillScreenPreview() {
    RefillScreen()
}