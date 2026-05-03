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

package com.laimburggasse.aquariumcontrol.refill.presentation.entry

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.refill.R
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillState
import com.laimburggasse.aquariumcontrol.refill.presentation.navigation.RefillNavigationEvent

@Composable
fun RefillScreen(
    refillStateUiState: RefillStateUiState,
    onNavigateFromRefill: (RefillNavigationEvent) -> Unit,
    onRefillEvent: (RefillUiEvent) -> Unit
) {
    // This screen displays
    // - the state of the refill control (ACTIVE, STOPPED, ERROR)
    // - a button for navigating to history screen
    // - a button for starting the refill controller
    // - a button for stopping the refill controller
    // - a button for resetting errors of the refill controller

    val messageStartRequestSent = stringResource(
        R.string.sent_request_to_controller_for_starting_refill_control
    )
    val messageStopRequestSent = stringResource(
        R.string.sent_request_to_controller_for_stopping_refill_control
    )
    val messageResetRequestSent = stringResource(
        R.string.sent_request_to_controller_for_resetting_errors
    )
    val context: Context = LocalContext.current // context for Toasts

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.refill_control))
        Scaffold(
            topBar = { },
            bottomBar = { },
            floatingActionButton = {},
            snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() })}
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PullToRefreshBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    state = rememberPullToRefreshState(),
                    isRefreshing = refillStateUiState is RefillStateUiState.Loading,
                    onRefresh = { onRefillEvent(RefillUiEvent.Refresh) },
                    indicator = {
                        if (refillStateUiState is RefillStateUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 150.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    Row(modifier = Modifier.padding(all = 8.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues = innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (refillStateUiState) {
                                is RefillStateUiState.Success -> {
                                    RefillStateComposable(refillStateUiState.refillState)
                                }
                                is RefillStateUiState.Error -> {
                                    Text(
                                        text = stringResource(R.string.error_loading_refill_data) +
                                                refillStateUiState.errorMessage,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.weight(weight = 1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                else -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(size = 150.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = { onNavigateFromRefill(RefillNavigationEvent.ToRefillHistory) },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.view_refill_events),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    onRefillEvent(RefillUiEvent.StartControl)
                                    Toast.makeText(
                                        context,
                                        messageStartRequestSent,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.start_refill_control),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    onRefillEvent(RefillUiEvent.StopControl)
                                    Toast.makeText(
                                        context,
                                        messageStopRequestSent,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.stop_refill_control),
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    onRefillEvent(RefillUiEvent.ResetErrors)
                                    Toast.makeText(
                                        context,
                                        messageResetRequestSent,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.reset_errors),
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
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=500dp,orientation=landscape"
)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun RefillScreenDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refill,
        screenContent = {
            RefillScreen(
                refillStateUiState = RefillStateUiState.Success(RefillState.ACTIVE),
                onNavigateFromRefill = { _ -> }
            ) { _ -> }
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=500dp,orientation=landscape"
)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun RefillScreenLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refill,
        screenContent = {
            RefillScreen(
                refillStateUiState = RefillStateUiState.Success(RefillState.ACTIVE),
                onNavigateFromRefill = { _ -> }
            ) { _ -> }
        },
        onNavigate = {},
        darkTheme = false
    )
}