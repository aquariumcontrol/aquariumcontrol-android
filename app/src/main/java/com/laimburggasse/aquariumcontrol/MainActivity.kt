package com.laimburggasse.aquariumcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.balling.BallingRepository
import com.laimburggasse.aquariumcontrol.balling.BallingScreen
import com.laimburggasse.aquariumcontrol.balling.BallingViewModelFactory
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditScreen
import com.laimburggasse.aquariumcontrol.balling.edit.BallingProfileEditViewModelFactory
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerAddScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerEditScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.controller.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.controller.ControllerViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.heating.HeatingScreen
import com.laimburggasse.aquariumcontrol.info.InfoScreen
import com.laimburggasse.aquariumcontrol.overview.OverviewScreen
import com.laimburggasse.aquariumcontrol.refill.RefillScreen
import com.laimburggasse.aquariumcontrol.refill.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontrol.schedule.ScheduleScreen
import com.laimburggasse.aquariumcontrol.timedata.TimeDataScreen
import com.laimburggasse.aquariumcontrol.ventilation.VentilationScreen

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

    private val feedProfilesViewModel by viewModels<FeedProfilesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FeedProfilesViewModel() as T
                }
            }
        }
    )

    private val ballingRepository by lazy { BallingRepository(mainViewModel = mainViewModel) }

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

        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val defaultProfileId = sharedPrefs.getLong(KEY_DEFAULT_PROFILE_ID, -1) // -1 for no default
        mainViewModel.setDefaultControllerProfileId(defaultProfileId)

        setContent {
            val navHostController = rememberNavController()
            val state by controllerViewModel.state.collectAsState()
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
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory24h) {
                        FeedHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedHistory7d) {
                        FeedHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedEdit) {
                        FeedProfileEditScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            feedProfile = mainViewModel.getMySelectedFeedProfile()!!
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedAddProfile) {
                        FeedProfileAddScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            feedProfilesViewModel = feedProfilesViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.feedSchedule,
                        arguments = listOf(
                            navArgument(name = "profileName") { type = NavType.StringType },
                            navArgument(name = "timestamp") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val profileName = backStackEntry.arguments?.getString("profileName")
                        val milliSecondsSinceEpoch = backStackEntry.arguments?.getLong("milliSecondsSinceEpoch")
                        if (profileName != null && milliSecondsSinceEpoch != null) {
                            FeedProfileScheduleScreen(
                                navHostController = navHostController,
                                mainViewModel = mainViewModel,
                                profileName = profileName,
                                milliSecondsSinceEpoch = milliSecondsSinceEpoch
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.balling) {
                        BallingScreen(
                            navHostController = navHostController,
                            ballingViewModelFactory = BallingViewModelFactory(ballingRepository),
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory24h) {
                        BallingHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.ballingHistory7d) {
                        BallingHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
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
                                    ballingRepository = ballingRepository
                                )
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. navigate back)
                        }
                    }
                    composable(route = ViewNavigationRoutes.refill) {
                        RefillScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory24h) {
                        RefillHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.refillHistory7d) {
                        RefillHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.heating) {
                        HeatingScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = ViewNavigationRoutes.ventilation) {
                        VentilationScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
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
                            mainViewModel = mainViewModel
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

