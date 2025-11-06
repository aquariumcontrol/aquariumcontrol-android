package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
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
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.BallingNavigationEvent
import com.laimburggasse.aquariumcontrol.balling.domain.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.presentation.components.BallingProfileSummaryComposable
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable

@Composable
fun BallingScreen(
    ballingProfilesUiState: BallingProfilesUiState,
    onNavigateFromBalling: (BallingNavigationEvent, Int?) -> Unit,
    onLaunchedEffect: () -> Unit
) {
    // This screen displays
    // - a button for visualizing all Balling dosing events of the last 24h
    // - a button for visualizing all Balling dosing events of the last 7 days
    // - all Balling profiles downloaded from the controller

    val configuration: Configuration = LocalConfiguration.current

    LaunchedEffect(key1 = true) {
        Log.d("BallingScreen", "LaunchedEffect")
        onLaunchedEffect()
    }

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
                    text = "Error loading data: ${ballingProfilesUiState.errorMessage}",
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
                val ballingProfiles: List<BallingProfile> = ballingProfilesUiState.ballingProfiles
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

val mockBallingProfile1 = BallingProfile(pumpId = 1, volume = 2.1, label = "mock1")
val mockBallingProfile2 = BallingProfile(pumpId = 2, volume = 3.1, label = "mock2")
val mockBallingProfiles: List<BallingProfile> = listOf(mockBallingProfile1, mockBallingProfile2)

val mockBallingProfilesUiStateSuccess = BallingProfilesUiState.Success(ballingProfiles = mockBallingProfiles)
val mockBallingProfilesUiStateError = BallingProfilesUiState.Error(errorMessage = "Error message")
val mockBallingProfilesUiStateLoading = BallingProfilesUiState.Loading
val mockBallingProfilesUiStateEmpty = BallingProfilesUiState.Empty

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
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateSuccess,
                onNavigateFromBalling = { event, pumpId -> },
                onLaunchedEffect = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateError,
                onNavigateFromBalling = { event, pumpId -> },
                onLaunchedEffect = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewLoading() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateLoading,
                onNavigateFromBalling = { event, pumpId -> },
                onLaunchedEffect = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenPreviewEmpty() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateEmpty,
                onNavigateFromBalling = { event, pumpId -> },
                onLaunchedEffect = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}