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

package com.laimburggasse.aquariumcontrol.balling.presentation.edit

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.balling.preview.BallingPreviewDataImpl.mockBallingProfileEditUiState
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.presentation.components.BallingProfileSummaryComposable
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable

@Composable
fun BallingProfileEditScreen(
    ballingProfileEditUiState: BallingProfileEditUiState,
    onEvent: (BallingProfileEditUiEvent) -> Unit
) {
    // This screen displays:
    // - Headline
    // - Edit field for the dosing volume of the selected balling profile
    // - Slider to adjust the dosing volume
    // - Text to display the current dosing volume
    // - Button to confirm the new dosing volume

    val context: Context = LocalContext.current
    val configuration: Configuration = LocalConfiguration.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(
            text = stringResource(id = R.string.balling_dosing)
        )
        Spacer(modifier = Modifier.size(size = 10.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 16.dp)
                .weight(weight = 1f)
                .fillMaxWidth()
        ) {
            when (ballingProfileEditUiState) {

                is BallingProfileEditUiState.Error -> {
                    val errorMessage: String = ballingProfileEditUiState.errorMessage
                    Text(text = stringResource(id = R.string.error_loading_balling_data) + errorMessage)
                }
                BallingProfileEditUiState.Loading -> {
                    Text(text = stringResource(id = R.string.loading_balling_profile))
                }
                BallingProfileEditUiState.NotFound -> {
                    Text(text = stringResource(id = R.string.balling_profile_not_found))
                }
                is BallingProfileEditUiState.Success -> {
                    val ballingProfile: BallingProfile = ballingProfileEditUiState.ballingProfile

                    var sliderPositionNewDosingVolume: Double by remember {
                        mutableDoubleStateOf(value = ballingProfile.volume)
                    }

                    LaunchedEffect(key1 = ballingProfile.volume) {
                        sliderPositionNewDosingVolume = ballingProfile.volume
                    }

                    BallingProfileSummaryComposable(
                        ballingProfile = ballingProfile,
                        onNavigateFromBalling = {event, pumpId -> }
                    )

                    when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(all = 20.dp)
                            ) {
                                BallingProfileEditTextFieldSliderComposable(
                                    sliderPositionNewDosingVolume,
                                    onEvent
                                )
                            }
                        }

                        else -> {
                            BallingProfileEditTextFieldSliderComposable(
                                sliderPositionNewDosingVolume,
                                onEvent
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        onClick = {
                            onEvent(BallingProfileEditUiEvent.ConfirmDosingVolume(
                                newDosingValue = sliderPositionNewDosingVolume)
                            )
                            Toast.makeText(
                                context,
                                context.getString(R.string.sent_request_to_controller_for_updating_dosing_volume),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    ) {
                        Text(
                            text = stringResource(id = R.string.update_dosing_value),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}



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
internal fun BallingProfileEditScreenPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.balling,
        screenContent = {
            BallingProfileEditScreen(
                ballingProfileEditUiState = mockBallingProfileEditUiState,
                onEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}