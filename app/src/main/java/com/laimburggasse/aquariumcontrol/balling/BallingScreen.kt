package com.laimburggasse.aquariumcontrol.balling

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BallingScreen(
    navHostController: NavHostController? = null,
    mockBallingProfiles: List<BallingProfile>? = null,
    mainViewModel: MainViewModel,
    darkTheme: Boolean? = null,
) {
    // This screen displays
    // - a button for visualizing all Balling dosing events of the last 24h
    // - a button for visualizing all Balling dosing events of the last 7 days
    // - all Balling profiles downloaded from the controller

    // @todo: Add view model

    var ballingProfiles by remember { mutableStateOf(mockBallingProfiles) }

    val configuration = LocalConfiguration.current

    val url = "http://" + mainViewModel.getControllerAddress() + WebApiUrls.BALLING_GET_SET_VALS
    val reader = BallingProfileReader(url)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            ballingProfiles = reader.readBallingProfiles()
        }
    }

    AquariumControllerTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.balling_dosing)
                    )
                    when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                BallingDosingHistorySelectionButtonsComposable(
                                    navHostController = navHostController,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .weight(0.5f)
                                )
                            }
                        }
                        else -> {
                            BallingDosingHistorySelectionButtonsComposable(
                                navHostController = navHostController,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                            )
                        }
                    }
                    if (ballingProfiles != null) {
                        val modifier = Modifier.height(3000.dp) // Set a fixed height
                            .fillMaxWidth()
                            .padding(16.dp)
                        if (LocalInspectionMode.current) {
                            Column(modifier) {
                                ballingProfiles!!.forEach { ballingProfile ->
                                    BallingProfileSummaryComposable(
                                        navHostController = navHostController,
                                        mainViewModel = mainViewModel,
                                        ballingProfile = ballingProfile
                                    )
                                }
                            }
                        }
                        else {
                            LazyColumn(modifier = Modifier) {
                                items(ballingProfiles!!) { ballingProfile ->
                                    BallingProfileSummaryComposable(
                                        navHostController = navHostController,
                                        mainViewModel = mainViewModel,
                                        ballingProfile = ballingProfile
                                    )
                                }
                            }
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

val mockMainViewModel = MockMainViewModel()

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
internal fun BallingScreenPreview() {
    val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
    val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
    val mockBallingProfiles = listOf(mockBallingProfile1, mockBallingProfile2)

    BallingScreen(
        mockBallingProfiles = mockBallingProfiles,
        mainViewModel = mockMainViewModel,
    )
}