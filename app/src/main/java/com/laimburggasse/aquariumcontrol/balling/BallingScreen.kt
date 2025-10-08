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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun BallingScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    ballingViewModel: BallingViewModelInterface = viewModel<BallingViewModel>(),
    darkTheme: Boolean? = null,
) {
    // This screen displays
    // - a button for visualizing all Balling dosing events of the last 24h
    // - a button for visualizing all Balling dosing events of the last 7 days
    // - all Balling profiles downloaded from the controller

    val ballingUiState: BallingUiState by remember { ballingViewModel::ballingUiState }

    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        ballingViewModel.loadData(mainViewModel)
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
                    when (ballingUiState) {
                        is BallingUiState.Empty -> {
                            Text(
                                text = stringResource(id = R.string.no_balling_profiles_found),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                        is BallingUiState.Error -> {
                            Text(
                                text = "Error loading data: ${(ballingUiState as BallingUiState.Error).errorMessage}",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                        is BallingUiState.Loading -> {
                            Text(
                                text = stringResource(id = R.string.loading_balling_profiles),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                        is BallingUiState.Success -> {
                            val ballingProfiles: List<BallingProfile> =
                                (ballingUiState as BallingUiState.Success).ballingProfiles
                            val modifier: Modifier = Modifier
                                .height(3000.dp) // Set a fixed height
                                .fillMaxWidth()
                                .padding(16.dp)
                            if (LocalInspectionMode.current) {
                                // running in preview
                                Column(modifier) {
                                    ballingProfiles.forEach { ballingProfile ->
                                        BallingProfileSummaryComposable(
                                            navHostController = navHostController,
                                            mainViewModel = mainViewModel,
                                            ballingProfile = ballingProfile
                                        )
                                    }
                                }
                            }
                            else {
                                // running on device or emulator
                                LazyColumn(modifier = Modifier) {
                                    items(items = ballingProfiles) { ballingProfile ->
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

val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
val mockBallingProfiles: List<BallingProfile> = listOf(mockBallingProfile1, mockBallingProfile2)

val mockBallingUiStateSuccess = BallingUiState.Success(ballingProfiles = mockBallingProfiles)

val mockBallingViewModelSuccess = MockBallingViewModel(ballingUiState = mockBallingUiStateSuccess, isRefreshing = false)

val mockBallingUiStateError = BallingUiState.Error(errorMessage = "Error message")

val mockBallingViewModelError = MockBallingViewModel(ballingUiState = mockBallingUiStateError, isRefreshing = false)

val mockBallingUiStateLoading = BallingUiState.Loading

val mockBallingViewModelLoading = MockBallingViewModel(ballingUiState = mockBallingUiStateLoading, isRefreshing = false)

val mockBallingUiStateEmpty = BallingUiState.Empty

val mockBallingViewModelEmpty = MockBallingViewModel(ballingUiState = mockBallingUiStateEmpty, isRefreshing = false)


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
internal fun BallingScreenPreviewSuccess() {
    BallingScreen(
        mainViewModel = mockMainViewModel,
        ballingViewModel = mockBallingViewModelSuccess,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewError() {
    BallingScreen(
        mainViewModel = mockMainViewModel,
        ballingViewModel = mockBallingViewModelError,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewLoading() {
    BallingScreen(
        mainViewModel = mockMainViewModel,
        ballingViewModel = mockBallingViewModelLoading,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewEmpty() {
    BallingScreen(
        mainViewModel = mockMainViewModel,
        ballingViewModel = mockBallingViewModelEmpty,
    )
}