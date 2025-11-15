package com.laimburggasse.aquariumcontrol.schedule.presentation

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries

@Composable
fun ScheduleScreen(
    scheduleUiState: ScheduleUiState,
    onScheduleEvent: (ScheduleUiEvent) -> Unit
) {
    // This screen displays for each of (Balling, Refill, schedule, Heating)
    // - a range slider for setting the start and stop time

    val configuration: Configuration = LocalConfiguration.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(R.string.actuator_schedule))
        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
            state = rememberPullToRefreshState(),
            isRefreshing = scheduleUiState is ScheduleUiState.Loading,
            onRefresh = { onScheduleEvent(ScheduleUiEvent.Refresh) },
            indicator = {
                if (scheduleUiState is ScheduleUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 150.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            when (scheduleUiState) {
                is ScheduleUiState.Success -> {
                    val scheduleEntries: ScheduleEntries = scheduleUiState.scheduleEntries
                    when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row {
                                ScheduleUpdateResetButtonsComposable(onScheduleEvent)
                            }
                        }

                        else -> {
                            ScheduleUpdateResetButtonsComposable(onScheduleEvent)
                        }
                    }
                    Scaffold(
                        topBar = { },
                        bottomBar = { },
                        floatingActionButton = {},
                        snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() }) }
                    ) { innerPadding ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(paddingValues = innerPadding)
                        ) {
                            Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                            Column(
                                modifier = Modifier.verticalScroll(state = rememberScrollState())
                            ) {
                                // --- Balling Adjustment ---
                                ScheduleAdjustmentComposable(
                                    title = "Balling",
                                    startTime = scheduleEntries.ballingStartTime,
                                    stopTime = scheduleEntries.ballingStopTime,
                                    isLimitationActive = scheduleEntries.ballingLimitationActive,
                                    onStartTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.BALLING,
                                                TimeType.START,
                                                newTime
                                            )
                                        )
                                    },
                                    onStopTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.BALLING,
                                                TimeType.STOP,
                                                newTime
                                            )
                                        )
                                    },
                                    onLimitationActiveChanged = { isActive ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeLimitationActive(
                                                ScheduleType.BALLING, isActive
                                            )
                                        )
                                    }
                                )

                                // --- Refill Adjustment ---
                                ScheduleAdjustmentComposable(
                                    title = "Refill",
                                    startTime = scheduleEntries.refillStartTime,
                                    stopTime = scheduleEntries.refillStopTime,
                                    isLimitationActive = scheduleEntries.refillLimitationActive,
                                    onStartTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.REFILL,
                                                TimeType.START,
                                                newTime
                                            )
                                        )
                                    },
                                    onStopTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.REFILL,
                                                TimeType.STOP,
                                                newTime
                                            )
                                        )
                                    },
                                    onLimitationActiveChanged = { isActive ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeLimitationActive(
                                                ScheduleType.REFILL, isActive
                                            )
                                        )
                                    }
                                )

                                // --- Ventilation Adjustment ---
                                ScheduleAdjustmentComposable(
                                    title = "Ventilation",
                                    startTime = scheduleEntries.ventilationStartTime,
                                    stopTime = scheduleEntries.ventilationStopTime,
                                    isLimitationActive = scheduleEntries.ventilationLimitationActive,
                                    onStartTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.VENTILATION,
                                                TimeType.START,
                                                newTime
                                            )
                                        )
                                    },
                                    onStopTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.VENTILATION,
                                                TimeType.STOP,
                                                newTime
                                            )
                                        )
                                    },
                                    onLimitationActiveChanged = { isActive ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeLimitationActive(
                                                ScheduleType.VENTILATION, isActive
                                            )
                                        )
                                    }
                                )

                                // --- Ventilation Adjustment ---
                                ScheduleAdjustmentComposable(
                                    title = "Heating",
                                    startTime = scheduleEntries.heatingStartTime,
                                    stopTime = scheduleEntries.heatingStopTime,
                                    isLimitationActive = scheduleEntries.heatingLimitationActive,
                                    onStartTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.HEATING,
                                                TimeType.START,
                                                newTime
                                            )
                                        )
                                    },
                                    onStopTimeChanged = { newTime ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeTime(
                                                ScheduleType.HEATING,
                                                TimeType.STOP,
                                                newTime
                                            )
                                        )
                                    },
                                    onLimitationActiveChanged = { isActive ->
                                        onScheduleEvent(
                                            ScheduleUiEvent.ChangeLimitationActive(
                                                ScheduleType.HEATING, isActive
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                is ScheduleUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error loading data: ${scheduleUiState.errorMessage}")
                    }
                }

                ScheduleUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(id = R.string.loading_actuator_schedule))
                    }
                }

                ScheduleUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(id = R.string.no_schedule_configured_check_server_configuration))
                    }
                }
            }
        }
    }
}

val mockScheduleEntries = ScheduleEntries(
    ballingStartTime = 2F,
    ballingStopTime = 22F,
    ballingLimitationActive = false,
    ballingScheduleLoaded = true,
    refillStartTime = 3F,
    refillStopTime = 21F,
    refillLimitationActive = true,
    refillScheduleLoaded = true,
    ventilationStartTime = 4F,
    ventilationStopTime = 20F,
    ventilationLimitationActive = false,
    ventilationScheduleLoaded = true,
    heatingStartTime = 5F,
    heatingStopTime = 19F,
    heatingLimitationActive = true,
    heatingScheduleLoaded = true
)

val mockScheduleUiStateSuccess = ScheduleUiState.Success(
    scheduleEntries = mockScheduleEntries
)

val mockScheduleUiStateError = ScheduleUiState.Error(
    errorMessage = "Mock Error loading data"
)

@Preview(showBackground = true, heightDp = 1200, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1200, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.schedule,
        screenContent = {
            ScheduleScreen(
                scheduleUiState = mockScheduleUiStateSuccess,
                onScheduleEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.schedule,
        screenContent = {
            ScheduleScreen(
                scheduleUiState = mockScheduleUiStateError,
                onScheduleEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}