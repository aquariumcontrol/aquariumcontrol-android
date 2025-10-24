package com.laimburggasse.aquariumcontrol

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.controller.common.ControllerState
import com.laimburggasse.aquariumcontrol.controller.common.ControllerViewModel
import com.laimburggasse.aquariumcontrol.controller.common.handleControllerNavigation
import com.laimburggasse.aquariumcontrol.controller.edit.ControllerEditScreen
import com.laimburggasse.aquariumcontrol.controller.selection.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.entry.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.common.handleFeedNavigation
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditViewModel
import com.laimburggasse.aquariumcontrol.heating.HeatingScreen
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsViewModelFactory
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsViewModelFactory
import com.laimburggasse.aquariumcontrol.info.InfoScreen
import com.laimburggasse.aquariumcontrol.maindropdownmenu.handleMainDropDownMenuNavigation
import com.laimburggasse.aquariumcontrol.overview.OverviewRepository
import com.laimburggasse.aquariumcontrol.overview.OverviewScreen
import com.laimburggasse.aquariumcontrol.overview.OverviewViewModelFactory
import com.laimburggasse.aquariumcontrol.refill.RefillRepository
import com.laimburggasse.aquariumcontrol.refill.RefillScreen
import com.laimburggasse.aquariumcontrol.refill.RefillViewModelFactory
import com.laimburggasse.aquariumcontrol.refill.handleRefillNavigation
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.schedule.ScheduleRepository
import com.laimburggasse.aquariumcontrol.schedule.ScheduleScreen
import com.laimburggasse.aquariumcontrol.schedule.ScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.timedata.TimeDataRepository
import com.laimburggasse.aquariumcontrol.timedata.TimeDataScreen
import com.laimburggasse.aquariumcontrol.timedata.TimeDataViewModelFactory
import com.laimburggasse.aquariumcontrol.ventilation.VentilationScreen
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsRepository
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
    private val mainViewModel by viewModels<MainViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(sharedPreferences) as T
                }
            }
        }
    )

    private val controllerViewModel by viewModels<ControllerViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ControllerViewModel(
                        dao = db.controllerProfileDao(),
                        mainViewModel
                    ) as T
                }
            }
        }
    )

    private val overviewRepository by lazy { OverviewRepository(mainViewModel = mainViewModel) }

    private val overviewViewModelFactory by lazy { OverviewViewModelFactory(overviewRepository) }

    private val ballingProfilesRepository by lazy { BallingProfilesRepository(mainViewModel = mainViewModel) }
    private val ballingHistoryRepository by lazy { BallingHistoryRepository(mainViewModel = mainViewModel) }
    private val ballingProfilesViewModelFactory by lazy { BallingProfilesViewModelFactory(ballingProfilesRepository) }
    private val ballingHistoryViewModelFactory by lazy { BallingHistoryViewModelFactory(ballingHistoryRepository) }

    private val feedProfilesRepository by lazy { FeedProfilesRepository(mainViewModel = mainViewModel) }
    private val feedScheduleRepository by lazy { FeedScheduleRepository(mainViewModel = mainViewModel) }
    private val feedHistoryRepository by lazy { FeedHistoryRepository(mainViewModel = mainViewModel) }
    private val feedHistoryViewModelFactory by lazy { FeedHistoryViewModelFactory(feedHistoryRepository) }
    private val refillHistoryRepository by lazy { RefillHistoryRepository(mainViewModel = mainViewModel) }
    private val refillRepository by lazy { RefillRepository(mainViewModel = mainViewModel) }
    private val refillViewModelFactory by lazy { RefillViewModelFactory(refillRepository) }
    private val refillHistoryViewModelFactory by lazy { RefillHistoryViewModelFactory(refillHistoryRepository) }
    private val feedProfilesViewModelFactory by lazy { FeedProfilesViewModelFactory(feedProfilesRepository) }
    private val feedScheduleViewModelFactory by lazy { FeedScheduleViewModelFactory(feedScheduleRepository, feedProfilesRepository) }
    private val feedProfileAddViewModelFactory by lazy { FeedProfileAddViewModelFactory(feedProfilesRepository) }

    private val scheduleRepository by lazy { ScheduleRepository(mainViewModel = mainViewModel) }

    private val scheduleViewModelFactory by lazy { ScheduleViewModelFactory(scheduleRepository) }

    private val ventilationSetValsRepository by lazy { VentilationSetValsRepository(
        mainViewModel = mainViewModel,
        externalScope = lifecycleScope
    ) }

    private val ventilationSetValsViewModelFactory by lazy {
        VentilationSetValsViewModelFactory(
            ventilationSetValsRepository,
        )
    }

    private val timeDataRepository by lazy { TimeDataRepository(mainViewModel = mainViewModel) }

    private val timeDataViewModelFactory by lazy { TimeDataViewModelFactory(timeDataRepository) }

    private val heatingSetValsRepository by lazy { HeatingSetValsRepository(
        mainViewModel = mainViewModel,
        externalScope = lifecycleScope
    ) }

    private val heatingSetValsViewModelFactory by lazy {
        HeatingSetValsViewModelFactory(
            heatingSetValsRepository,
        )
    }

    private val heatingStatsRepository by lazy { HeatingStatsRepository(
        mainViewModel = mainViewModel,
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
            mainViewModel.defaultControllerProfileId.value.let {
                putLong(
                    KEY_DEFAULT_PROFILE_ID,
                    mainViewModel.defaultControllerProfileId.value!!
                )
                apply()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navHostController: NavHostController = rememberNavController()
            val state: ControllerState by controllerViewModel.state.collectAsState()

            NavHost(
                navController = navHostController,
                startDestination = ViewNavigationRoutes.controllerSelection,
                builder = {
                    composable(route = ViewNavigationRoutes.controllerSelection) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.controllerSelection,
                            screenContent = {
                                ControllerSelectionScreen(
                                    state,
                                    onEvent = controllerViewModel::onEvent,
                                    onNavigateFromController = { event, controllerProfileId ->
                                        navHostController.handleControllerNavigation(event, controllerProfileId)
                                    },
                                    defaultProfileId = mainViewModel.defaultControllerProfileId.collectAsState(),
                                    canStartWaitForNavigation = controllerViewModel.canStartWaitForDefaultNavigation.collectAsState(),
                                    progressToDefaultNavigation = controllerViewModel.progressToDefaultNavigation.collectAsState()
                               ) },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.controllerAdd)
                    {
                        mainViewModel.setFirstLoad()
                        MainScaffold(
                            mainViewModel = mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                            screenContent = {
                                ControllerAddScreen(
                                    state,
                                    onEvent = controllerViewModel::onEvent,
                                    onNavigateFromController = { event, controllerProfileId ->
                                        navHostController.handleControllerNavigation(
                                            event,
                                            controllerProfileId
                                        )
                                    }
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
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
                        mainViewModel.setFirstLoad()
                        val controllerProfileId: Long = backStackEntry.arguments?.getLong("controllerProfileId") ?: -1L
                        if (controllerProfileId >= 0L) {
                            MainScaffold(
                                mainViewModel = mainViewModel,
                                navigationRouteSource = ViewNavigationRoutes.controllerEdit,
                                screenContent = {
                                    ControllerEditScreen(
                                        state,
                                        onEvent = controllerViewModel::onEvent,
                                        isDefaultProfileInitialValue = mainViewModel.defaultControllerProfileId.collectAsState().value == controllerProfileId,
                                        onNavigateFromController = { event, controllerProfileId ->
                                            navHostController.handleControllerNavigation(event, controllerProfileId)
                                        }
                                    )
                                },
                                onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                            )
                        }
                    }
                    composable(route = ViewNavigationRoutes.feed) {
                        val feedProfilesViewModel: FeedProfilesViewModel = viewModel(factory = feedProfilesViewModelFactory)
                        val feedScheduleViewModel: FeedScheduleViewModel = viewModel(factory = feedScheduleViewModelFactory)
                        val feedProfilesUiState: FeedProfilesUiState by feedProfilesViewModel.feedProfilesUiState.collectAsState()
                        val feedScheduleUiState: FeedScheduleUiState by feedScheduleViewModel.feedScheduleUiState.collectAsState()

                        MainScaffold(
                            mainViewModel = mainViewModel,
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
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory24h) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.feedHistory24h,
                            screenContent = {
                                FeedHistoryScreen(
                                    showFullWeek = false,
                                    mainViewModel = mainViewModel,
                                    feedHistoryViewModelFactory = feedHistoryViewModelFactory,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory7d) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.feedHistory7d,
                            screenContent = {
                                FeedHistoryScreen(
                                    showFullWeek = true,
                                    mainViewModel = mainViewModel,
                                    feedHistoryViewModelFactory = feedHistoryViewModelFactory,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
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
                                    mainViewModel = mainViewModel,
                                    navigationRouteSource = ViewNavigationRoutes.feedEdit,
                                    screenContent = { FeedProfileEditScreen(
                                        uiState,
                                        onFeedProfileEditUiEvent = feedProfileEditViewModel::onFeedProfileEditUiEvent,
                                    ) },
                                    onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
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
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
                            screenContent = {
                                FeedProfileAddScreen(
                                    feedProfileAddViewModelFactory = feedProfileAddViewModelFactory,
                                    onNavigateFromFeed = { event, feedProfileId, isNewScheduleEntry, millisSinceEpoch ->
                                        navHostController.handleFeedNavigation(
                                            event,
                                            feedProfileId,
                                            isNewScheduleEntry,
                                            millisSinceEpoch
                                        )
                                    }
                                )
                            },
                            onNavigate = { event ->
                                navHostController.handleMainDropDownMenuNavigation(event)
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
                                    mainViewModel,
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
                                            event
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
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.balling,
                            screenContent = {
                                BallingScreen(
                                    ballingProfilesUiState,
                                    onNavigateFromBalling = {
                                        event, pumpId -> navHostController.handleBallingNavigation(event, pumpId)
                                    }
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory24h) {
                        val ballingHistoryViewModel: BallingHistoryViewModel = viewModel(
                            factory = ballingHistoryViewModelFactory
                        )
                        val ballingHistoryUiState: BallingHistoryUiState by
                            ballingHistoryViewModel.ballingHistoryUiState.collectAsState()
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                            screenContent = {
                                BallingHistoryScreen(
                                    showFullWeek = false,
                                    ballingHistoryUiState,
                                    onEvent = ballingHistoryViewModel::onEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory7d) {
                        val ballingHistoryViewModel: BallingHistoryViewModel = viewModel(
                            factory = ballingHistoryViewModelFactory
                        )
                        val ballingHistoryUiState: BallingHistoryUiState by
                        ballingHistoryViewModel.ballingHistoryUiState.collectAsState()
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.ballingHistory24h,
                            screenContent = {
                                BallingHistoryScreen(
                                    showFullWeek = true,
                                    ballingHistoryUiState,
                                    onEvent = ballingHistoryViewModel::onEvent
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
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
                                mainViewModel,
                                navigationRouteSource = ViewNavigationRoutes.ballingEdit,
                                screenContent = {
                                    BallingProfileEditScreen(
                                        ballingProfileEditUiState,
                                        onEvent = ballingProfileEditViewModel::onEvent,
                                    )
                                },
                                onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.refill) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.refill,
                            screenContent = {
                                RefillScreen(
                                    mainViewModel,
                                    refillViewModelFactory,
                                    onNavigateFromRefill = {
                                        event -> navHostController.handleRefillNavigation(event)
                                    }
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory24h) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.refillHistory24h,
                            screenContent = {
                                RefillHistoryScreen(
                                    showFullWeek = false,
                                    mainViewModel = mainViewModel,
                                    refillHistoryViewModelFactory,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory7d) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.refillHistory7d,
                            screenContent = {
                                RefillHistoryScreen(
                                    showFullWeek = true,
                                    mainViewModel,
                                    refillHistoryViewModelFactory,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.heating) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.heating,
                            screenContent = {
                                HeatingScreen(
                                    heatingSetValsViewModelFactory,
                                    heatingStatsViewModelFactory,
                                )
                            },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.ventilation) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.ventilation,
                            screenContent = { VentilationScreen(ventilationSetValsViewModelFactory) },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) }
                        )
                    }
                    composable(route = ViewNavigationRoutes.timeData) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.timeData,
                            screenContent = { TimeDataScreen(mainViewModel, timeDataViewModelFactory) },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.schedule) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.schedule,
                            screenContent = { ScheduleScreen(scheduleViewModelFactory) },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
                        )
                    }
                    composable(route = ViewNavigationRoutes.info) {
                        MainScaffold(
                            mainViewModel,
                            navigationRouteSource = ViewNavigationRoutes.info,
                            screenContent = { InfoScreen() },
                            onNavigate = { event -> navHostController.handleMainDropDownMenuNavigation(event) },
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
                        Log.d("MainActivity", "Navigating to overview screen")
                        if (controllerProfileId != null) {
                            val controllerProfile =
                                controllerViewModel.getSelectedControllerProfileById(
                                    controllerProfileId
                                )
                            if (controllerProfile != null) {
                                Log.d("MainActivity", "Controller profile is not null")
                                mainViewModel.setSelectedControllerProfile(controllerProfile)
                                mainViewModel.setFirstLoad() // Controller selection may navigate here without user interaction
                                MainScaffold(
                                    mainViewModel,
                                    navigationRouteSource = ViewNavigationRoutes.overView,
                                    screenContent = {
                                        OverviewScreen(
                                            controllerProfile = controllerProfile,
                                            overviewViewModelFactory
                                        )
                                    },
                                    onNavigate = { event ->
                                        navHostController.handleMainDropDownMenuNavigation(event)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}