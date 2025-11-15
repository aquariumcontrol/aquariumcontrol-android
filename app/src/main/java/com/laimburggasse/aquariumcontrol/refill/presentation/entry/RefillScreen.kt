package com.laimburggasse.aquariumcontrol.refill.presentation.entry

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.refill.presentation.navigation.RefillNavigationEvent

@Composable
fun RefillScreen(
    onNavigateFromRefill: (RefillNavigationEvent) -> Unit,
    onRefillEvent: (RefillUiEvent) -> Unit
) {
    // This screen displays
    // - a button for visualizing all refill events of the last 24h
    // - a button for visualizing all refill events of the last 7 days
    // - a button for starting the refill controller
    // - a button for stopping the refill controller
    // - a button for resetting errors of the refill controller

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
                Row(modifier = Modifier.padding(all = 8.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues = innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            onClick = { onNavigateFromRefill(RefillNavigationEvent.ToRefillHistory24h) },
                        ) {
                            Text(
                                text = stringResource(id = R.string.view_refill_events_24h),
                                fontSize = 16.sp
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            onClick = { onNavigateFromRefill(RefillNavigationEvent.ToRefillHistory7d) },
                        ) {
                            Text(
                                text = stringResource(id = R.string.view_refill_events_7d),
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
                                    context.getString(R.string.sent_request_to_controller_for_starting_refill_control),
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
                                    context.getString(R.string.sent_request_to_controller_for_stopping_refill_control),
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
                                    context.getString(R.string.sent_request_to_controller_for_resetting_errors),
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun RefillScreenPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.refill,
        screenContent = {
            RefillScreen(
                onNavigateFromRefill = { event -> },
                onRefillEvent = { event -> }
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}