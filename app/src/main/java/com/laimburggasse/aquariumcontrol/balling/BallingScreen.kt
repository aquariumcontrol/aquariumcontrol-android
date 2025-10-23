package com.laimburggasse.aquariumcontrol.balling

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun BallingScreen(
    ballingViewModelFactory: BallingViewModelFactory,
    onNavigateFromBalling: (BallingNavigationEvent, Int?) -> Unit
) {
    // This screen displays
    // - a button for visualizing all Balling dosing events of the last 24h
    // - a button for visualizing all Balling dosing events of the last 7 days
    // - all Balling profiles downloaded from the controller

    val ballingViewModel: BallingProfilesViewModel = viewModel(factory = ballingViewModelFactory)

    val ballingProfilesUiState: BallingProfilesUiState by ballingViewModel.ballingProfilesUiState.collectAsState()

    val configuration: Configuration = LocalConfiguration.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.balling_dosing))
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BallingDosingHistorySelectionButtonsComposable(
                        modifier = Modifier.padding(vertical = 8.dp).weight(weight = 0.5f),
                        onNavigateFromBalling
                    )
                }
            }
            else -> {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onNavigateFromBalling
                )
            }
        }
        when (ballingProfilesUiState) {
            is BallingProfilesUiState.Empty -> {
                Text(
                    text = stringResource(id = R.string.no_balling_profiles_found),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(weight = 1f),
                    textAlign = TextAlign.Center
                )
            }
            is BallingProfilesUiState.Error -> {
                Text(
                    text = "Error loading data: ${(ballingProfilesUiState as BallingProfilesUiState.Error).errorMessage}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(weight = 1f),
                    textAlign = TextAlign.Center
                )
            }
            is BallingProfilesUiState.Loading -> {
                Text(
                    text = stringResource(id = R.string.loading_balling_profiles),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(weight = 1f),
                    textAlign = TextAlign.Center
                )
            }
            is BallingProfilesUiState.Success -> {
                val ballingProfiles: List<BallingProfile> =
                    (ballingProfilesUiState as BallingProfilesUiState.Success).ballingProfiles
                val modifier: Modifier = Modifier
                    .height(height = 3000.dp) // Set a fixed height
                    .fillMaxWidth()
                    .padding(all = 16.dp)
                if (LocalInspectionMode.current) {
                    // running in preview
                    Column(modifier) {
                        ballingProfiles.forEach { ballingProfile ->
                            BallingProfileSummaryComposable(
                                ballingProfile = ballingProfile,
                                onNavigateFromBalling
                            )
                        }
                    }
                }
                else {
                    // running on device or emulator
                    LazyColumn(modifier = Modifier) {
                        items(items = ballingProfiles) { ballingProfile ->
                            BallingProfileSummaryComposable(
                                ballingProfile = ballingProfile,
                                onNavigateFromBalling
                            )
                        }
                    }
                }
            }
        }
    }
}

val mockMainViewModel = MockMainViewModel()

val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
val mockBallingProfiles: List<BallingProfile> = listOf(mockBallingProfile1, mockBallingProfile2)
val mockRepositoryEmpty = MockBallingProfilesRepository(mockData = DataFetchResult.Success(data = emptyList()))
val mockBallingViewModelFactoryEmpty = BallingViewModelFactory(ballingProfilesRepository = mockRepositoryEmpty)
val mockBallingData = DataFetchResult.Success(data = mockBallingProfiles)
val mockBallingRepositorySuccess = MockBallingProfilesRepository(mockData = mockBallingData)
val mockBallingViewModelFactorySuccess = BallingViewModelFactory(ballingProfilesRepository = mockBallingRepositorySuccess)

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
    MainScaffold(
        mainViewModel = mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingViewModelFactory = mockBallingViewModelFactorySuccess,
                onNavigateFromBalling = { event, pumpId -> }
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewError() {
    val mockBallingData = DataFetchResult.Error(exception = null, errorMessage = "Error message")
    val mockBallingRepositoryError = MockBallingProfilesRepository(mockData = mockBallingData)
    val mockBallingViewModelFactory = BallingViewModelFactory(ballingProfilesRepository = mockBallingRepositoryError)
    MainScaffold(
        mainViewModel = mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingViewModelFactory = mockBallingViewModelFactory,
                onNavigateFromBalling = { event, pumpId -> }
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewLoading() {
    val mockBallingRepositoryLoading = MockLongLoadingBallingProfilesRepository()
    val mockBallingViewModelFactory = BallingViewModelFactory(ballingProfilesRepository = mockBallingRepositoryLoading)
    MainScaffold(
        mainViewModel = mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingViewModelFactory = mockBallingViewModelFactory,
                onNavigateFromBalling = { event, pumpId -> }
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewEmpty() {
    MainScaffold(
        mainViewModel = mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingViewModelFactory = mockBallingViewModelFactoryEmpty,
                onNavigateFromBalling = { event, pumpId -> }
            )
        },
        onNavigate = {}
    )
}