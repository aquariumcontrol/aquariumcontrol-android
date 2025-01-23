package com.example.aquariumcontroller.balling

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BallingScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    mockBallingProfiles: List<BallingProfile>? = null
) {
    var ballingProfiles by remember { mutableStateOf(mockBallingProfiles) }

    if (mainViewModel != null) {
        val url = "http://" + mainViewModel.getControllerAddress() + ApiUrls.BALLING_GET_SET_VALS
        val reader = BallingProfileReader(url)

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                ballingProfiles = reader.readBallingProfiles()
            }
        }
    }

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                MainDropdownMenuScreen(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.balling,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = stringResource(id = R.string.BallingMineralDosingControl),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navHostController?.navigate(ViewNavigationRoutes.ballingHistory24h)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewBallingEvents24h),
                        fontSize = 16.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navHostController?.navigate(ViewNavigationRoutes.ballingHistory7d)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewBallingEvents7d),
                        fontSize = 16.sp
                    )
                }
                if (ballingProfiles != null) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        items(ballingProfiles!!) { ballingProfile ->
                            BallingProfileSummaryScreen(
                                navHostController = navHostController,
                                mainViewModel = mainViewModel,
                                ballingProfile = ballingProfile
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
fun BallingViewPreview() {
    val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
    val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
    val mockBallingProfiles = listOf(mockBallingProfile1, mockBallingProfile2)

    AquariumControllerTheme {
        BallingScreen(mockBallingProfiles = mockBallingProfiles)
    }
}