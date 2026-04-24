/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.presentation.components.BallingProfileSummaryComposable
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.BallingNavigationEvent
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingProfilesUiStateEmpty
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingProfilesUiStateError
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingProfilesUiStateLoading
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingProfilesUiStateSuccess
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes

@Composable
fun BallingScreen(
    ballingProfilesUiState: BallingProfilesUiState,
    onNavigateFromBalling: (BallingNavigationEvent, Int?) -> Unit,
    onRefresh: () -> Unit
) {
    // This screen displays
    // - a button for visualizing all Balling dosing events of the last 24h
    // - a button for visualizing all Balling dosing events of the last 7 days
    // - all Balling profiles downloaded from the controller

    val configuration: Configuration = LocalConfiguration.current

    LaunchedEffect(key1 = true) {
        onRefresh()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.balling_dosing))
        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPullToRefreshState(),
            isRefreshing = ballingProfilesUiState == BallingProfilesUiState.Loading,
            contentAlignment = Alignment.Center,
            onRefresh = { onRefresh() },
            indicator = {
                if (ballingProfilesUiState == BallingProfilesUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 150.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                        val ballingProfiles: List<BallingProfile> =
                            ballingProfilesUiState.ballingProfiles
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
                        } else {
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
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateSuccess,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateSuccess,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingScreenErrorDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateError,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenErrorLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateError,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingScreenLoadingDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateLoading,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenLoadingLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateLoading,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingScreenEmptyDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateEmpty,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingScreenEmptyLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingScreen(
                ballingProfilesUiState = mockBallingProfilesUiStateEmpty,
                onNavigateFromBalling = { _, _ -> },
                onRefresh = {}
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}