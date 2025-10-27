package com.laimburggasse.aquariumcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.balling.common.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.balling.common.handleBallingNavigation
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditScreen
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditUiState
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditViewModel
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.balling.entry.BallingProfilesUiState
import com.laimburggasse.aquariumcontrol.balling.entry.BallingProfilesViewModel
import com.laimburggasse.aquariumcontrol.balling.entry.BallingProfilesViewModelFactory
import com.laimburggasse.aquariumcontrol.balling.entry.BallingScreen
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryUiState
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryViewModel
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.controller.add.ControllerAddScreen
import com.laimburggasse.aquariumcontrol.controller.add.ControllerAddViewModel
import com.laimburggasse.aquariumcontrol.controller.add.ControllerAddViewModelFactory
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.controller.common.ControllerState
import com.laimburggasse.aquariumcontrol.controller.common.handleControllerNavigation
import com.laimburggasse.aquariumcontrol.controller.edit.ControllerEditScreen
import com.laimburggasse.aquariumcontrol.controller.edit.ControllerEditViewModel
import com.laimburggasse.aquariumcontrol.controller.edit.ControllerEditViewModelFactory
import com.laimburggasse.aquariumcontrol.controller.selection.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.controller.selection.ControllerSelectionViewModel
import com.laimburggasse.aquariumcontrol.controller.selection.ControllerSelectionViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddUiState
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddViewModel
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.common.handleFeedNavigation
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditViewModel
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.entry.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryUiState
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryViewModel
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingScreen
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsViewModel
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsViewModelFactory
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsViewModel
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsViewModelFactory
import com.laimburggasse.aquariumcontrol.info.InfoScreen
import com.laimburggasse.aquariumcontrol.maindropdownmenu.handleMainDropDownMenuNavigation
import com.laimburggasse.aquariumcontrol.overview.OverviewRepository
import com.laimburggasse.aquariumcontrol.overview.OverviewScreen
import com.laimburggasse.aquariumcontrol.overview.OverviewUiState
import com.laimburggasse.aquariumcontrol.overview.OverviewViewModel
import com.laimburggasse.aquariumcontrol.overview.OverviewViewModelFactory
import com.laimburggasse.aquariumcontrol.refill.common.handleRefillNavigation
import com.laimburggasse.aquariumcontrol.refill.entry.RefillRepository
import com.laimburggasse.aquariumcontrol.refill.entry.RefillScreen
import com.laimburggasse.aquariumcontrol.refill.entry.RefillViewModel
import com.laimburggasse.aquariumcontrol.refill.entry.RefillViewModelFactory
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryUiState
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryViewModel
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.schedule.ScheduleRepository
import com.laimburggasse.aquariumcontrol.schedule.ScheduleScreen
import com.laimburggasse.aquariumcontrol.schedule.ScheduleUiState
import com.laimburggasse.aquariumcontrol.schedule.ScheduleViewModel
import com.laimburggasse.aquariumcontrol.schedule.ScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.timedata.TimeDataRepository
import com.laimburggasse.aquariumcontrol.timedata.TimeDataScreen
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState
import com.laimburggasse.aquariumcontrol.timedata.TimeDataViewModel
import com.laimburggasse.aquariumcontrol.timedata.TimeDataViewModelFactory
import com.laimburggasse.aquariumcontrol.ventilation.VentilationScreen
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsUiState
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsViewModel
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsViewModelFactory

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        const val KEY_DEFAULT_PROFILE_ID = "default_profile_id"

        const val NO_DEFAULT_ID = -1L
    }

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ControllerProfileDatabase::class.java,
            "controller_profiles.db"
        ).build()
    }

    private val sharedPreferences by lazy {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private val sessionManager by lazy {
        SessionManager(
            sharedPreferences,
            dao = db.controllerProfileDao()
        )
    }

    private val controllerSelectionViewModelFactory by lazy {
        ControllerSelectionViewModelFactory(
            dao = db.controllerProfileDao(),
            sessionManager,
        )
    }

    private val controllerAddViewModelFactory by lazy {
        ControllerAddViewModelFactory(
            dao = db.controllerProfileDao(),
        )
    }
    private val overviewRepository by lazy { OverviewRepository(sessionManager = sessionManager) }

    private val overviewViewModelFactory by lazy { OverviewViewModelFactory(overviewRepository) }

    private val ballingProfilesRepository by lazy { BallingProfilesRepository(sessionManager = sessionManager) }
    private val ballingHistoryRepository by lazy { BallingHistoryRepository(sessionManager = sessionManager) }
    private val ballingProfilesViewModelFactory by lazy { BallingProfilesViewModelFactory(ballingProfilesRepository) }
    private val ballingHistoryViewModelFactory by lazy { BallingHistoryViewModelFactory(ballingHistoryRepository) }

    private val feedProfilesRepository by lazy { FeedProfilesRepository(sessionManager = sessionManager) }
    private val feedScheduleRepository by lazy { FeedScheduleRepository(sessionManager = sessionManager) }
    private val feedHistoryRepository by lazy { FeedHistoryRepository(sessionManager = sessionManager) }
    private val feedHistoryViewModelFactory by lazy { FeedHistoryViewModelFactory(feedHistoryRepository) }
    private val refillHistoryRepository by lazy { RefillHistoryRepository(sessionManager = sessionManager) }
    private val refillRepository by lazy { RefillRepository(sessionManager = sessionManager) }
    private val refillViewModelFactory by lazy { RefillViewModelFactory(refillRepository) }
    private val refillHistoryViewModelFactory by lazy { RefillHistoryViewModelFactory(refillHistoryRepository) }
    private val feedProfilesViewModelFactory by lazy { FeedProfilesViewModelFactory(feedProfilesRepository) }
    private val feedScheduleViewModelFactory by lazy { FeedScheduleViewModelFactory(feedScheduleRepository, feedProfilesRepository) }
    private val feedProfileAddViewModelFactory by lazy { FeedProfileAddViewModelFactory(feedProfilesRepository) }

    private val scheduleRepository by lazy { ScheduleRepository(sessionManager = sessionManager) }

    private val scheduleViewModelFactory by lazy { ScheduleViewModelFactory(scheduleRepository) }

    private val ventilationSetValsRepository by lazy { VentilationSetValsRepository(
        sessionManager = sessionManager,
        externalScope = lifecycleScope
    ) }

    private val ventilationSetValsViewModelFactory by lazy {
        VentilationSetValsViewModelFactory(
            ventilationSetValsRepository,
        )
    }

    private val timeDataRepository by lazy { TimeDataRepository(sessionManager = sessionManager) }

    private val timeDataViewModelFactory by lazy { TimeDataViewModelFactory(timeDataRepository) }

    private val heatingSetValsRepository by lazy { HeatingSetValsRepository(
        sessionManager = sessionManager,
        externalScope = lifecycleScope
    ) }

    private val heatingSetValsViewModelFactory by lazy {
        HeatingSetValsViewModelFactory(
            heatingSetValsRepository,
        )
    }

    private val heatingStatsRepository by lazy { HeatingStatsRepository(
        sessionManager = sessionManager,
        externalScope = lifecycleScope
    ) }

    private val heatingStatsViewModelFactory by lazy {
        HeatingStatsViewModelFactory(
            heatingStatsRepository
        )
    }

    override fun onPause() {
        super.onPause()
        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            sessionManager.defaultControllerProfileId.value.let {
                putLong(
                    KEY_DEFAULT_PROFILE_ID,
                    sessionManager.defaultControllerProfileId.value!!
                )
                apply()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navHostController: NavHostController = rememberNavController()
            val selectedControllerProfile: ControllerProfile? by sessionManager.selectedControllerProfile.collectAsState()
            val isControllerLoaded: Boolean by sessionManager.isControllerLoaded.collectAsState()
            
            NavHost(
                navController = navHostController,
                startDestination = ViewNavigationRoutes.controllerSelection,
                builder = {
                    composable(route = ViewNavigationRoutes.controllerSelection) {
                        val controllerSelectionViewModel: ControllerSelectionViewModel = viewModel(
                            factory = controllerSelectionViewModelFactory
                        )
                        val state: ControllerState by controllerSelectionViewModel.state.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.controllerSelection,
                            screenContent = {
                                ControllerSelectionScreen(
                                    state,
                                    onEvent = controllerSelectionViewModel::onEvent,
                                    onNavigateFromController = { event, controllerProfileId ->
                                        navHostController.handleControllerNavigation(event, controllerProfileId)
                                    },
                                    defaultProfileId = sessionManager.defaultControllerProfileId.collectAsState(),
                                    canStartWaitForNavigation = controllerSelectionViewModel.canStartWaitForDefaultNavigation.collectAsState(),
                                    progressToDefaultNavigation = controllerSelectionViewModel.progressToDefaultNavigation.collectAsState()
                               ) },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.controllerAdd)
                    {
                        val controllerAddViewModel: ControllerAddViewModel = viewModel(
                            factory = controllerAddViewModelFactory
                        )
                        val state: ControllerState by controllerAddViewModel.state.collectAsState()
                        sessionManager.setFirstLoad()
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
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.controllerEdit,
                        arguments = listOf(
                            navArgument(name = "controllerProfileId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    )
                    { backStackEntry ->
                        sessionManager.setFirstLoad()
                        val controllerProfileId: Long = backStackEntry.arguments?.getLong("controllerProfileId") ?: -1L
                        val controllerEditViewModelFactory: ControllerEditViewModelFactory by lazy {
                            ControllerEditViewModelFactory(
                                dao = db.controllerProfileDao(),
                                sessionManager,
                                controllerProfileId
                            )
                        }
                        val controllerEditViewModel: ControllerEditViewModel = viewModel(
                            factory = controllerEditViewModelFactory
                        )
                        val editState: ControllerState by controllerEditViewModel.state.collectAsState()

                        if (controllerProfileId >= 0L) {
                            MainScaffold(
                                isControllerLoaded,
                                navigationRouteSource = ViewNavigationRoutes.controllerEdit,
                                screenContent = {
                                    ControllerEditScreen(
                                        state = editState,
                                        onEvent = controllerEditViewModel::onEvent,
                                        isDefaultProfileInitialValue = sessionManager.defaultControllerProfileId.collectAsState().value == controllerProfileId,
                                        onNavigateFromController = { event, controllerProfileId ->
                                            navHostController.handleControllerNavigation(event, controllerProfileId)
                                        }
                                    )
                                },
                                onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                            )
                        }
                    }
                    composable(route = ViewNavigationRoutes.feed) {
                        val feedProfilesViewModel: FeedProfilesViewModel = viewModel(factory = feedProfilesViewModelFactory)
                        val feedScheduleViewModel: FeedScheduleViewModel = viewModel(factory = feedScheduleViewModelFactory)
                        val feedProfilesUiState: FeedProfilesUiState by feedProfilesViewModel.feedProfilesUiState.collectAsState()
                        val feedScheduleUiState: FeedScheduleUiState by feedScheduleViewModel.feedScheduleUiState.collectAsState()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feed,
                            screenContent = {
                                FeedScreen(
                                    feedProfilesUiState,
                                    feedScheduleUiState,
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
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory24h) {
                        val feedHistoryViewModel: FeedHistoryViewModel = viewModel(
                            factory = feedHistoryViewModelFactory
                        )
                        val feedHistoryUiState: FeedHistoryUiState by
                            feedHistoryViewModel.feedHistoryUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedHistory24h,
                            screenContent = {
                                FeedHistoryScreen(
                                    showFullWeek = false,
                                    feedHistoryUiState,
                                    onFeedHistoryEvent = feedHistoryViewModel::onEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory7d) {
                        val feedHistoryViewModel: FeedHistoryViewModel = viewModel(
                            factory = feedHistoryViewModelFactory
                        )
                        val feedHistoryUiState: FeedHistoryUiState by
                            feedHistoryViewModel.feedHistoryUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedHistory7d,
                            screenContent = {
                                FeedHistoryScreen(
                                    showFullWeek = true,
                                    feedHistoryUiState,
                                    onFeedHistoryEvent = feedHistoryViewModel::onEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedEdit,
                        arguments = listOf(
                            navArgument(name = "feedProfileId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val feedProfileId: Int? = backStackEntry.arguments?.getInt("feedProfileId")
                        if (feedProfileId != null) {
                            val feedProfile: FeedProfile? = feedProfilesRepository.findProfileById(feedProfileId)
                            if (feedProfile != null) {
                                // view model factory needs to be created with dedicated feed profile
                                val feedProfileEditViewModelFactory = FeedProfileEditViewModelFactory(
                                        feedProfile = feedProfile,
                                        feedProfilesRepository = feedProfilesRepository
                                    )
                                val feedProfileEditViewModel: FeedProfileEditViewModel = viewModel(
                                    factory = feedProfileEditViewModelFactory
                                )
                                val uiState by feedProfileEditViewModel.uiState.collectAsState()
                                MainScaffold(
                                    isControllerLoaded,
                                    navigationRouteSource = ViewNavigationRoutes.feedEdit,
                                    screenContent = { FeedProfileEditScreen(
                                        uiState,
                                        onFeedProfileEditUiEvent = feedProfileEditViewModel::onFeedProfileEditUiEvent,
                                    ) },
                                    onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                                )
                            }
                            else {
                                // @todo: handle error case (e.g. navigate back)
                            }
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.feedAddProfile) {
                        val feedProfileAddViewModel: FeedProfileAddViewModel = viewModel(
                            factory = feedProfileAddViewModelFactory
                        )
                        val feedProfileName: String by feedProfileAddViewModel.feedProfileName.collectAsState()
                        val feedProfileAddUiState: FeedProfileAddUiState by feedProfileAddViewModel.feedProfileAddUiState.collectAsState()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
                            screenContent = {
                                FeedProfileAddScreen(
                                    feedProfileName,
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
                                navHostController.handleMainDropDownMenuNavigation(event, sessionManager)
                            },
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedSchedule,
                        arguments = listOf(
                            navArgument(name = ViewNavigationRoutes.FEED_ARG_PROFILE_ID) { type = NavType.IntType },
                            navArgument(name = ViewNavigationRoutes.FEED_ARG_IS_NEW_SCHEDULE_ENTRY) { type = NavType.BoolType },
                            navArgument(name = ViewNavigationRoutes.FEED_ARG_MILLIS_SINCE_EPOCH) { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val feedProfileId: Int? = backStackEntry.arguments?.getInt(
                            ViewNavigationRoutes.FEED_ARG_PROFILE_ID
                        )
                        val isNewScheduleEntry: Boolean? = backStackEntry.arguments?.getBoolean(
                            ViewNavigationRoutes.FEED_ARG_IS_NEW_SCHEDULE_ENTRY
                        )
                        val millisSinceEpoch: Long? = backStackEntry.arguments?.getLong(
                            ViewNavigationRoutes.FEED_ARG_MILLIS_SINCE_EPOCH
                        )
                        if (feedProfileId != null && isNewScheduleEntry != null && millisSinceEpoch != null) {
                            val feedProfilesViewModel: FeedProfilesViewModel = viewModel(factory = feedProfilesViewModelFactory)
                            val feedScheduleViewModel: FeedScheduleViewModel = viewModel(factory = feedScheduleViewModelFactory)
                            val feedProfile = feedProfilesViewModel.findProfileById(profileID = feedProfileId)
                            if (feedProfile != null) {
                                MainScaffold(
                                    isControllerLoaded,
                                    navigationRouteSource = ViewNavigationRoutes.feedSchedule,
                                    screenContent = {
                                        FeedProfileScheduleScreen(
                                            feedProfile,
                                            isNewScheduleEntry,
                                            millisSinceEpoch,
                                            onNavigateFromFeed = { event, profileId, isNew, millis ->
                                                navHostController.handleFeedNavigation(
                                                    event,
                                                    feedProfileId,
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
                                )
                            }
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.balling) {
                        val ballingViewModel: BallingProfilesViewModel =
                            viewModel(factory = ballingProfilesViewModelFactory)
                        val ballingProfilesUiState: BallingProfilesUiState by
                            ballingViewModel.ballingProfilesUiState.collectAsState()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.balling,
                            screenContent = {
                                BallingScreen(
                                    ballingProfilesUiState,
                                    onNavigateFromBalling = {
                                        event, pumpId -> navHostController.handleBallingNavigation(event, pumpId)
                                    }
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory24h) {
                        val ballingHistoryViewModel: BallingHistoryViewModel = viewModel(
                            factory = ballingHistoryViewModelFactory
                        )
                        val ballingHistoryUiState: BallingHistoryUiState by
                            ballingHistoryViewModel.ballingHistoryUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                            screenContent = {
                                BallingHistoryScreen(
                                    showFullWeek = false,
                                    ballingHistoryUiState,
                                    onEvent = ballingHistoryViewModel::onEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory7d) {
                        val ballingHistoryViewModel: BallingHistoryViewModel = viewModel(
                            factory = ballingHistoryViewModelFactory
                        )
                        val ballingHistoryUiState: BallingHistoryUiState by
                        ballingHistoryViewModel.ballingHistoryUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                            screenContent = {
                                BallingHistoryScreen(
                                    showFullWeek = true,
                                    ballingHistoryUiState,
                                    onEvent = ballingHistoryViewModel::onEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingEdit,
                        arguments = listOf(
                            navArgument(name = "ballingProfileId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val ballingProfileId: Int? = backStackEntry.arguments?.getInt("ballingProfileId")
                        if (ballingProfileId != null) {
                            // view model factory needs to be created with dedicated balling profile
                            val ballingProfileEditViewModelFactory = BallingProfileEditViewModelFactory(
                                ballingProfileId,
                                ballingProfilesRepository
                            )
                            val ballingProfileEditViewModel: BallingProfileEditViewModel = viewModel(
                                factory = ballingProfileEditViewModelFactory
                            )
                            val ballingProfileEditUiState: BallingProfileEditUiState =
                                ballingProfileEditViewModel.ballingProfileEditUiState
                            MainScaffold(
                                isControllerLoaded,
                                navigationRouteSource = ViewNavigationRoutes.ballingEdit,
                                screenContent = {
                                    BallingProfileEditScreen(
                                        ballingProfileEditUiState,
                                        onEvent = ballingProfileEditViewModel::onEvent,
                                    )
                                },
                                onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.refill) {
                        val refillViewModel: RefillViewModel = viewModel(factory = refillViewModelFactory)
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.refill,
                            screenContent = {
                                RefillScreen(
                                    onNavigateFromRefill = {
                                        event -> navHostController.handleRefillNavigation(event)
                                    },
                                    onRefillEvent = refillViewModel::onRefillEvent,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory24h) {
                        val refillHistoryViewModel: RefillHistoryViewModel = viewModel(
                            factory = refillHistoryViewModelFactory
                        )
                        val refillHistoryUiState: RefillHistoryUiState by refillHistoryViewModel.refillHistoryUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
                            screenContent = {
                                RefillHistoryScreen(
                                    showFullWeek = false,
                                    refillHistoryUiState,
                                    onRefillHistoryEvent = refillHistoryViewModel::onRefillHistoryEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory7d) {
                        val refillHistoryViewModel: RefillHistoryViewModel = viewModel(
                            factory = refillHistoryViewModelFactory
                        )
                        val refillHistoryUiState: RefillHistoryUiState by refillHistoryViewModel.refillHistoryUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.refillHistory7d,
                            screenContent = {
                                RefillHistoryScreen(
                                    showFullWeek = true,
                                    refillHistoryUiState,
                                    onRefillHistoryEvent = refillHistoryViewModel::onRefillHistoryEvent,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.heating) {
                        val heatingSetValsViewModel: HeatingSetValsViewModel = viewModel(factory = heatingSetValsViewModelFactory)
                        val heatingStatsViewModel: HeatingStatsViewModel = viewModel(factory = heatingStatsViewModelFactory)
                        val heatingSetValsUiState by heatingSetValsViewModel.heatingSetValsUiState.collectAsState()
                        val heatingStatsUiState by heatingStatsViewModel.heatingStatsUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.heating,
                            screenContent = {
                                HeatingScreen(
                                    heatingSetValsUiState,
                                    onHeatingSetValsEvent = heatingSetValsViewModel::onHeatingSetValsEvent,
                                    heatingStatsUiState,
                                    onHeatingStatsEvent = heatingStatsViewModel::onHeatingStatsEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ventilation) {
                        val ventilationSetValsViewModel: VentilationSetValsViewModel =
                            viewModel(factory = ventilationSetValsViewModelFactory)
                        val ventilationSetValsUiState: VentilationSetValsUiState by
                            ventilationSetValsViewModel.ventilationSetValsUiState.collectAsState()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.ventilation,
                            screenContent = {
                                VentilationScreen(
                                    ventilationSetValsUiState,
                                    onVentilationSetValsEvent = ventilationSetValsViewModel::onVentilationSetValsEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.timeData) {
                        val timeDataViewModel: TimeDataViewModel = viewModel(factory = timeDataViewModelFactory)
                        val timeDataUiState: TimeDataUiState by timeDataViewModel.timeDataUiState.collectAsState()

                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.timeData,
                            screenContent = {
                                TimeDataScreen(
                                    timeDataUiState,
                                    onTimeDataEvent = timeDataViewModel::onTimeDataEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.schedule) {
                        val scheduleViewModel: ScheduleViewModel = viewModel(factory = scheduleViewModelFactory)
                        val scheduleUiState: ScheduleUiState by scheduleViewModel.scheduleUiState.collectAsState()
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.schedule,
                            screenContent = {
                                ScheduleScreen(
                                    scheduleUiState,
                                    onScheduleEvent = scheduleViewModel::onScheduleEvent)
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.info) {
                        MainScaffold(
                            isControllerLoaded,
                            navigationRouteSource = ViewNavigationRoutes.info,
                            screenContent = { InfoScreen() },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event, sessionManager) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.overView,
                        arguments = listOf(
                            navArgument(name = "controllerProfileId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { backStackEntry ->
                        val controllerProfileId: Long? = backStackEntry.arguments?.getLong("controllerProfileId")
                        if (controllerProfileId != null) {
                            LaunchedEffect(key1 = controllerProfileId) {
                                if (controllerProfileId >= 0L) {
                                    sessionManager.selectControllerById(controllerProfileId)
                                    sessionManager.setFirstLoad() // Controller selection may navigate here without user interaction
                                }
                            }
                            val overviewViewModel: OverviewViewModel = viewModel(factory = overviewViewModelFactory)
                            val overviewUiState: OverviewUiState by overviewViewModel.overviewUiState.collectAsState()
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
                                    navHostController.handleMainDropDownMenuNavigation(event, sessionManager)
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}