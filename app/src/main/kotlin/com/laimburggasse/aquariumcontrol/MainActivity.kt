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

package com.laimburggasse.aquariumcontrol

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditScreen
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesUiState
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingScreen
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.handleBallingNavigation
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation.handleMainDropDownMenuNavigation
import com.laimburggasse.aquariumcontrol.controller.presentation.add.ControllerAddScreen
import com.laimburggasse.aquariumcontrol.controller.presentation.add.ControllerAddViewModel
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.controller.presentation.edit.ControllerEditScreen
import com.laimburggasse.aquariumcontrol.controller.presentation.edit.ControllerEditViewModel
import com.laimburggasse.aquariumcontrol.controller.presentation.navigation.handleControllerNavigation
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerSelectionViewModel
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleSelectionUiState
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.history.FeedHistoryViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.handleFeedNavigation
import com.laimburggasse.aquariumcontrol.feed.presentation.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingScreen
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsUiState
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsViewModel
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsUiState
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsViewModel
import com.laimburggasse.aquariumcontrol.info.presentation.InfoScreen
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewScreen
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewUiState
import com.laimburggasse.aquariumcontrol.overview.presentation.OverviewViewModel
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillScreen
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillScreenEvent
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillStateUiState
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillViewModel
import com.laimburggasse.aquariumcontrol.refill.presentation.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.refill.presentation.history.RefillHistoryViewModel
import com.laimburggasse.aquariumcontrol.refill.presentation.navigation.handleRefillNavigation
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleScreen
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleUiState
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleViewModel
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataScreen
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataUiState
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataViewModel
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationScreen
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationSetValsUiState
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationSetValsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onPause() {
        super.onPause()
        if (sessionManager.defaultControllerProfileId.value != null) {
            val sharedPrefs: SharedPreferences =
                getSharedPreferences(GlobalConstants.PREFS_NAME, MODE_PRIVATE)
            with(receiver = sharedPrefs.edit()) {
                sessionManager.defaultControllerProfileId.value.let {
                    putLong(
                        GlobalConstants.KEY_DEFAULT_PROFILE_ID,
                        sessionManager.defaultControllerProfileId.value!!
                    )
                    apply()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navHostController: NavHostController = rememberNavController()
            val selectedControllerProfile: ControllerProfile? by
                sessionManager.selectedControllerProfile.collectAsStateWithLifecycle()
            val isControllerLoaded: Boolean by
                sessionManager.isControllerLoaded.collectAsStateWithLifecycle()

            NavHost(
                navController = navHostController,
                startDestination = ViewNavigationRoutes.controllerSelection,
                builder = {
                    composable(route = ViewNavigationRoutes.controllerSelection) {
                        val controllerSelectionViewModel: ControllerSelectionViewModel =
                            hiltViewModel()
                        val controllerProfileList: List<ControllerProfile> by
                            controllerSelectionViewModel.controllerProfiles.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.controllerSelection,
                            screenContent = {
                                ControllerSelectionScreen(
                                    controllerProfileList,
                                    onEvent = controllerSelectionViewModel::onEvent,
                                    onNavigateFromController = { event, controllerProfileId ->
                                        navHostController.handleControllerNavigation(
                                            event,
                                            controllerProfileId
                                        )
                                    },
                                    defaultProfileId = sessionManager.defaultControllerProfileId.
                                        collectAsStateWithLifecycle(),
                                    canStartWaitForNavigation = controllerSelectionViewModel.
                                        canStartWaitForDefaultNavigation.collectAsStateWithLifecycle(),
                                    progressToDefaultNavigation = controllerSelectionViewModel.
                                        progressToDefaultNavigation.collectAsStateWithLifecycle()
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.controllerAdd)
                    {
                        val controllerAddViewModel: ControllerAddViewModel = hiltViewModel()
                        val state: ControllerAddEditUiState by
                            controllerAddViewModel.state.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                            screenContent = {
                                ControllerAddScreen(
                                    state,
                                    onEvent = controllerAddViewModel::onEvent,
                                    onAddEvent = {
                                        controllerAddViewModel.addControllerProfile(
                                            event = it,
                                            onAddComplete = { navHostController.popBackStack() }
                                        )
                                    }
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(
                        route = ViewNavigationRoutes.controllerEdit,
                        arguments = listOf(
                            navArgument(name = "controllerProfileId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    )
                    { backStackEntry ->
                        val controllerProfileId: Long =
                            backStackEntry.arguments?.getLong("controllerProfileId") ?: -1L
                        val controllerEditViewModel: ControllerEditViewModel = hiltViewModel()
                        val editState: ControllerAddEditUiState by
                            controllerEditViewModel.state.collectAsStateWithLifecycle()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.controllerEdit,
                            screenContent = {
                                ControllerEditScreen(
                                    state = editState,
                                    onEvent = controllerEditViewModel::onEvent,
                                    isDefaultProfileInitialValue = sessionManager.defaultControllerProfileId.
                                        collectAsStateWithLifecycle().value == controllerProfileId,
                                    onNavigateFromController = { event, controllerProfileId ->
                                        navHostController.handleControllerNavigation(
                                            event,
                                            controllerProfileId
                                        )
                                    }
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.feed) {
                        val feedProfilesViewModel: FeedProfilesViewModel = hiltViewModel()
                        val feedScheduleViewModel: FeedScheduleViewModel = hiltViewModel()
                        val feedProfilesUiState: FeedProfilesUiState by
                            feedProfilesViewModel.feedProfilesUiState.collectAsStateWithLifecycle()
                        val feedScheduleSelectionUiState: FeedScheduleSelectionUiState by
                            feedScheduleViewModel.feedScheduleSelectionUiState.collectAsStateWithLifecycle()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feed,
                            screenContent = {
                                FeedScreen(
                                    feedProfilesUiState,
                                    feedScheduleSelectionUiState,
                                    onNavigateFromFeed = { event, feedProfileId, isNewScheduleEntry, millisSinceEpoch ->
                                        navHostController.handleFeedNavigation(
                                            event,
                                            feedProfileId,
                                            isNewScheduleEntry,
                                            millisSinceEpoch
                                        )
                                    },
                                    onFeedProfilesUiEvent = feedProfilesViewModel::onFeedProfilesUiEvent,
                                    onFeedSchedulesUiEvent = feedScheduleViewModel::onFeedSchedulesUiEvent,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory) {
                        val feedHistoryViewModel: FeedHistoryViewModel = hiltViewModel()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedHistory,
                            screenContent = {
                                FeedHistoryScreen(
                                    feedHistoryViewModel.feedHistoryFlow
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(
                        route = ViewNavigationRoutes.feedEdit,
                        arguments = listOf(
                            navArgument(name = "feedProfileId") { type = NavType.IntType }
                        )
                    ) { _ ->
                        val feedProfileEditViewModel: FeedProfileEditViewModel = hiltViewModel()
                        val uiState: FeedProfileEditUiState by
                            feedProfileEditViewModel.uiState.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedEdit,
                            screenContent = {
                                FeedProfileEditScreen(
                                    uiState,
                                    onFeedProfileEditUiEvent = feedProfileEditViewModel::onFeedProfileEditUiEvent,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedAddProfile) {
                        val feedProfileAddViewModel: FeedProfileAddViewModel = hiltViewModel()
                        val feedProfileName: String by
                            feedProfileAddViewModel.feedProfileName.collectAsStateWithLifecycle()
                        val feedProfileNameExists: Boolean by
                            feedProfileAddViewModel.feedProfileNameExists.collectAsStateWithLifecycle()
                        val feedProfileAddUiState: FeedProfileAddUiState by
                            feedProfileAddViewModel.feedProfileAddUiState.collectAsStateWithLifecycle()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
                            screenContent = {
                                FeedProfileAddScreen(
                                    feedProfileName,
                                    feedProfileNameExists,
                                    feedProfileAddUiState,
                                    onNavigateFromFeed = { event, feedProfileId, isNewScheduleEntry, millisSinceEpoch ->
                                        navHostController.handleFeedNavigation(
                                            event,
                                            feedProfileId,
                                            isNewScheduleEntry,
                                            millisSinceEpoch
                                        )
                                    },
                                    onFeedProfileAddEvent = feedProfileAddViewModel::onFeedProfileAddEvent,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(
                        route = ViewNavigationRoutes.feedSchedule,
                        arguments = listOf(
                            navArgument(name = ViewNavigationRoutes.FEED_ARG_PROFILE_ID) {
                                type = NavType.IntType
                            },
                            navArgument(name = ViewNavigationRoutes.FEED_ARG_IS_NEW_SCHEDULE_ENTRY) {
                                type = NavType.BoolType
                            },
                            navArgument(name = ViewNavigationRoutes.FEED_ARG_MILLIS_SINCE_EPOCH) {
                                type = NavType.LongType
                            }
                        )
                    ) { _ ->
                        val feedScheduleViewModel: FeedScheduleViewModel = hiltViewModel()
                        val feedProfile: FeedProfile? by
                            feedScheduleViewModel.feedProfile.collectAsStateWithLifecycle()
                        val isNewScheduleEntry: Boolean by
                            feedScheduleViewModel.isNewScheduleEntry.collectAsStateWithLifecycle()
                        val millisSinceEpoch: Long by
                            feedScheduleViewModel.millisSinceEpoch.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedSchedule,
                            screenContent = {
                                FeedProfileScheduleScreen(
                                    feedProfile,
                                    isNewScheduleEntry,
                                    millisSinceEpoch,
                                    onNavigateFromFeed = { event, _, _, _ ->
                                        navHostController.handleFeedNavigation(
                                            event,
                                            feedProfileId = feedProfile?.profileID,
                                            isNewScheduleEntry,
                                            millisSinceEpoch
                                        )
                                    },
                                    onFeedScheduleEntryUiEvent = feedScheduleViewModel::onFeedScheduleEntryUiEvent
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event, sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.balling) {
                        val ballingViewModel: BallingProfilesViewModel = hiltViewModel()
                        val ballingProfilesUiState: BallingProfilesUiState by
                        ballingViewModel.ballingProfilesUiState.collectAsStateWithLifecycle()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.balling,
                            screenContent = {
                                BallingScreen(
                                    ballingProfilesUiState,
                                    onNavigateFromBalling = { event, pumpId ->
                                        navHostController.handleBallingNavigation(event, pumpId)
                                    },
                                    onRefresh = ballingViewModel::onRefresh,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory) {
                        val ballingHistoryViewModel: BallingHistoryViewModel = hiltViewModel()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.ballingHistory,
                            screenContent = {
                                BallingHistoryScreen(
                                    ballingHistoryFlow = ballingHistoryViewModel.ballingHistoryFlow
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(
                        route = ViewNavigationRoutes.ballingEdit,
                        arguments = listOf(
                            navArgument(name = "ballingProfileId") { type = NavType.IntType }
                        )
                    ) { _ ->
                        // view model needs to be created with dedicated balling profile
                        val ballingProfileEditViewModel: BallingProfileEditViewModel =
                            hiltViewModel()
                        val ballingProfileEditUiState: BallingProfileEditUiState by
                        ballingProfileEditViewModel.ballingProfileEditUiState.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.ballingEdit,
                            screenContent = {
                                BallingProfileEditScreen(
                                    ballingProfileEditUiState,
                                    onEvent = ballingProfileEditViewModel::onEvent,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.refill) {
                        val refillViewModel: RefillViewModel = hiltViewModel()
                        val refillStateUiState: RefillStateUiState by
                                refillViewModel.refillStateUiState.collectAsStateWithLifecycle()
                        val context: Context = LocalContext.current

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.refill,
                            screenContent = {
                                LaunchedEffect(key1 = true) {
                                    refillViewModel.events.collect { event ->
                                        when (event) {
                                            is RefillScreenEvent.ShowToast -> {
                                                Toast.makeText(
                                                    context,
                                                    event.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                                RefillScreen(
                                    refillStateUiState,
                                    onNavigateFromRefill = { event ->
                                        navHostController.handleRefillNavigation(event)
                                    },
                                    onRefillEvent = refillViewModel::onRefillEvent,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory) {
                        val refillHistoryViewModel: RefillHistoryViewModel = hiltViewModel()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.refillHistory,
                            screenContent = {
                                RefillHistoryScreen(
                                    refillHistoryViewModel.refillHistoryFlow
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.heating) {
                        val heatingSetValsViewModel: HeatingSetValsViewModel = hiltViewModel()
                        val heatingStatsViewModel: HeatingStatsViewModel = hiltViewModel()
                        val heatingSetValsUiState: HeatingSetValsUiState by
                        heatingSetValsViewModel.heatingSetValsUiState.collectAsStateWithLifecycle()
                        val heatingStatsUiState: HeatingStatsUiState by
                        heatingStatsViewModel.heatingStatsUiState.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.heating,
                            screenContent = {
                                HeatingScreen(
                                    heatingSetValsUiState,
                                    heatingSetValsValid = heatingSetValsViewModel.
                                        heatingSetValsValid.collectAsStateWithLifecycle(),
                                    onHeatingSetValsEvent = heatingSetValsViewModel::onHeatingSetValsEvent,
                                    heatingStatsUiState,
                                    onHeatingStatsEvent = heatingStatsViewModel::onHeatingStatsEvent,
                                    isDarkMode = isSystemInDarkTheme()
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.ventilation) {
                        val ventilationSetValsViewModel: VentilationSetValsViewModel =
                            hiltViewModel()
                        val ventilationSetValsUiState: VentilationSetValsUiState by
                            ventilationSetValsViewModel.ventilationSetValsUiState.collectAsStateWithLifecycle()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.ventilation,
                            screenContent = {
                                VentilationScreen(
                                    ventilationSetValsUiState,
                                    ventilationSetValsValid = ventilationSetValsViewModel.
                                        ventilationSetValsValid.collectAsStateWithLifecycle(),
                                    onVentilationSetValsEvent = ventilationSetValsViewModel::onVentilationSetValsEvent,
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.timeData) {
                        val timeDataViewModel: TimeDataViewModel = hiltViewModel()
                        val timeDataUiState: TimeDataUiState by
                            timeDataViewModel.timeDataUiState.collectAsStateWithLifecycle()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.timeData,
                            screenContent = {
                                TimeDataScreen(
                                    timeDataUiState,
                                    onTimeDataEvent = timeDataViewModel::onTimeDataEvent,
                                    isDarkMode = isSystemInDarkTheme()
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.schedule) {
                        val scheduleViewModel: ScheduleViewModel = hiltViewModel()
                        val scheduleUiState: ScheduleUiState by
                            scheduleViewModel.scheduleUiState.collectAsStateWithLifecycle()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.schedule,
                            screenContent = {
                                ScheduleScreen(
                                    scheduleUiState,
                                    onScheduleEvent = scheduleViewModel::onScheduleEvent
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(route = ViewNavigationRoutes.info) {
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.info,
                            screenContent = { InfoScreen() },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(
                                    event,
                                    sessionManager
                                )
                            },
                            darkTheme = isSystemInDarkTheme()
                        )
                    }
                    composable(
                        route = ViewNavigationRoutes.overView,
                        arguments = listOf(
                            navArgument(name = "controllerProfileId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { backStackEntry ->
                        val controllerProfileId: Long? =
                            backStackEntry.arguments?.getLong("controllerProfileId")
                        if (controllerProfileId != null) {
                            LaunchedEffect(key1 = controllerProfileId) {
                                if (controllerProfileId >= 0L) {
                                    sessionManager.selectControllerById(controllerProfileId)
                                    sessionManager.suppressAutoNavigation() // Controller selection may navigate here without user interaction
                                }
                            }
                            val overviewViewModel: OverviewViewModel = hiltViewModel()
                            val overviewUiState: OverviewUiState by
                                overviewViewModel.overviewUiState.collectAsStateWithLifecycle()
                            MainScaffold(
                                isControllerLoaded,
                                navigationRouteSource = ViewNavigationRoutes.overView,
                                screenContent = {
                                    OverviewScreen(
                                        selectedControllerProfile,
                                        overviewUiState,
                                        onOverviewEvent = overviewViewModel::onOverviewEvent,
                                    )
                                },
                                onNavigate = { event ->
                                    navHostController.handleMainDropDownMenuNavigation(
                                        event,
                                        sessionManager
                                    )
                                },
                                darkTheme = isSystemInDarkTheme()
                            )
                        }
                    }
                }
            )
        }
    }
}