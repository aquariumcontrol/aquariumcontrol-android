package com.example.aquariumcontroller.refill

import android.content.Context
import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.MainViewModelFactory
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun callWebApi(
    apiUrl: String,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    postRequest: PostRequest,
    context: Context
) {
    val user = mainViewModel.getControllerUser()
    val password = mainViewModel.getControllerPassword()

    if (user != null && password != null) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + apiUrl

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                postRequest.postData(url, user, password)
            }
        }
    } else {
        Toast.makeText(
            context,
            "Currently selected controller does not have " +
                    "proper configuration of user and password",
            Toast.LENGTH_SHORT
        ).show()
    }

}

@Composable
fun RefillScreen(navHostController: NavHostController?) {
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory
    )

    val postRequest = PostRequest()

    val context = LocalContext.current

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
                    MainDropdownMenuScreen(
                        navHostController,
                        ViewNavigationRoutes.refill
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
                                    callWebApi(
                                        mainViewModel = mainViewModel,
                                        apiUrl = ApiUrls.REFILL_POST_CONTROL_START,
                                        coroutineScope = coroutineScope,
                                        postRequest = postRequest,
                                        context = context,
                                    )
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
                                    callWebApi(
                                        mainViewModel = mainViewModel,
                                        apiUrl = ApiUrls.REFILL_POST_CONTROL_STOP,
                                        coroutineScope = coroutineScope,
                                        postRequest = postRequest,
                                        context = context,
                                    )
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
                                    callWebApi(
                                        mainViewModel = mainViewModel,
                                        apiUrl = ApiUrls.REFILL_POST_CONTROL_RESET,
                                        coroutineScope = coroutineScope,
                                        postRequest = postRequest,
                                        context = context,
                                    )
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

@Preview(showBackground = true)
@Composable
fun RefillScreenPreview() {
    AquariumControllerTheme {
        RefillScreen(null)
    }
}