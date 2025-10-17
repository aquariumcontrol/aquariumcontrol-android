package com.laimburggasse.aquariumcontrol

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.balling.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.balling.BallingScreen
import com.laimburggasse.aquariumcontrol.balling.BallingViewModelFactory
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditScreen
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.controller.ControllerAddScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerEditScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.controller.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerState
import com.laimburggasse.aquariumcontrol.controller.ControllerViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.heating.HeatingScreen
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingSetValsViewModelFactory
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.heating.HeatingStatsViewModelFactory
import com.laimburggasse.aquariumcontrol.info.InfoScreen
import com.laimburggasse.aquariumcontrol.overview.OverviewScreen
import com.laimburggasse.aquariumcontrol.refill.RefillRepository
import com.laimburggasse.aquariumcontrol.refill.RefillScreen
import com.laimburggasse.aquariumcontrol.refill.RefillViewModelFactory
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryViewModelFactory
import com.laimburggasse.aquariumcontrol.schedule.ScheduleRepository
import com.laimburggasse.aquariumcontrol.schedule.ScheduleScreen
import com.laimburggasse.aquariumcontrol.schedule.ScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.timedata.TimeDataScreen
import com.laimburggasse.aquariumcontrol.ventilation.VentilationScreen
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.VentilationSetValsViewModelFactory

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_DEFAULT_PROFILE_ID = "default_profile_id"
    }

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ControllerProfileDatabase::class.java,
            "controller_profiles.db"
        ).build()
    }

    private val mainViewModel by viewModels<MainViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel() as T
                }
            }
        }
    )

    private val controllerViewModel by viewModels<ControllerViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ControllerViewModel(
                        db.controllerProfileDao()
                    ) as T
                }
            }
        }
    )

    private val ballingProfilesRepository by lazy { BallingProfilesRepository(mainViewModel = mainViewModel) }
    private val ballingHistoryRepository by lazy { BallingHistoryRepository(mainViewModel = mainViewModel) }
    private val feedProfilesRepository by lazy { FeedProfilesRepository(mainViewModel = mainViewModel) }
    private val feedScheduleRepository by lazy { FeedScheduleRepository(mainViewModel = mainViewModel) }
    private val feedHistoryRepository by lazy { FeedHistoryRepository(mainViewModel = mainViewModel) }
    private val refillHistoryRepository by lazy { RefillHistoryRepository(mainViewModel = mainViewModel) }
    private val refillRepository by lazy { RefillRepository(mainViewModel = mainViewModel) }
    private val refillViewModelFactory by lazy { RefillViewModelFactory(refillRepository) }

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
            putLong(KEY_DEFAULT_PROFILE_ID, mainViewModel.getDefaultControllerProfileId())
            apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val defaultProfileId: Long = sharedPrefs.getLong( KEY_DEFAULT_PROFILE_ID, -1) // -1 for no default
        mainViewModel.setDefaultControllerProfileId(defaultProfileId)

        setContent {
            val navHostController: NavHostController = rememberNavController()
            val state: ControllerState by controllerViewModel.state.collectAsState()
            NavHost(
                navController = navHostController,
                startDestination = ViewNavigationRoutes.controllerSelection,
                builder = {
                    composable(route = ViewNavigationRoutes.controllerSelection)
                    {
                        ControllerSelectionScreen(
                            navHostController = navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.controllerAdd)
                    {
                        ControllerAddScreen(
                            navHostController = navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.controllerEdit)
                    {
                        ControllerEditScreen(
                            navHostController = navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.feed) {
                        FeedScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            feedProfilesViewModelFactory = feedProfilesViewModelFactory,
                            feedScheduleViewModelFactory = feedScheduleViewModelFactory
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory24h) {
                        FeedHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel,
                            feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                                feedHistoryRepository
                            )
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory7d) {
                        FeedHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel,
                            feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                                feedHistoryRepository
                            )
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
                                val feedProfileEditViewModelFactory =
                                    FeedProfileEditViewModelFactory(
                                        feedProfile = feedProfile,
                                        feedProfilesRepository = feedProfilesRepository
                                    )
                                FeedProfileEditScreen(
                                    navHostController = navHostController,
                                    mainViewModel = mainViewModel,
                                    feedProfileEditViewModelFactory = feedProfileEditViewModelFactory,
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
                        FeedProfileAddScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            feedProfileAddViewModelFactory = feedProfileAddViewModelFactory,
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
                            FeedProfileScheduleScreen(
                                navHostController = navHostController,
                                mainViewModel = mainViewModel,
                                feedProfileId = feedProfileId,
                                feedScheduleViewModelFactory = feedScheduleViewModelFactory,
                                feedProfilesViewModelFactory = feedProfilesViewModelFactory,
                                isNewScheduleEntry = isNewScheduleEntry,
                                millisSinceEpoch = millisSinceEpoch,
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.balling) {
                        BallingScreen(
                            navHostController = navHostController,
                            ballingViewModelFactory = BallingViewModelFactory(ballingProfilesRepository),
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory24h) {
                        BallingHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            ballingHistoryViewModelFactory = BallingHistoryViewModelFactory(
                                ballingHistoryRepository
                            ),
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory7d) {
                        BallingHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            ballingHistoryViewModelFactory = BallingHistoryViewModelFactory(
                                ballingHistoryRepository
                            ),
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingEdit,
                        arguments = listOf(
                            navArgument(name = "ballingProfileId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val ballingProfileId: Int? = backStackEntry.arguments?.getInt("ballingProfileId")
                        if (ballingProfileId != null) {
                            BallingProfileEditScreen(
                                navHostController = navHostController,
                                mainViewModel = mainViewModel,
                                ballingProfileEditViewModelFactory = BallingProfileEditViewModelFactory(
                                    ballingProfileId = ballingProfileId,
                                    ballingProfilesRepository = ballingProfilesRepository
                                )
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.refill) {
                        RefillScreen(
                            navHostController,
                            mainViewModel,
                            refillViewModelFactory,
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory24h) {
                        RefillHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                                refillHistoryRepository
                            ),
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory7d) {
                        RefillHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel,
                            refillHistoryViewModelFactory = RefillHistoryViewModelFactory(
                                refillHistoryRepository
                            )
                        )
                    }
                    composable(route = ViewNavigationRoutes.heating) {
                        HeatingScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            heatingSetValsViewModelFactory,
                            heatingStatsViewModelFactory
                        )
                    }
                    composable(route = ViewNavigationRoutes.ventilation) {
                        VentilationScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            ventilationSetValsViewModelFactory
                        )
                    }
                    composable(route = ViewNavigationRoutes.timeData) {
                        TimeDataScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.schedule) {
                        ScheduleScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            scheduleViewModelFactory,
                        )
                    }
                    composable(route = ViewNavigationRoutes.info) {
                        InfoScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.overView) {
                        OverviewScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                }
            )
        }
    }
}

