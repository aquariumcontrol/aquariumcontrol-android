package com.laimburggasse.aquariumcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.laimburggasse.aquariumcontroller.balling.BallingScreen
import com.laimburggasse.aquariumcontroller.balling.edit.BallingProfileEditScreen
import com.laimburggasse.aquariumcontroller.balling.history.BallingHistoryScreen
import com.laimburggasse.aquariumcontroller.controller.ControllerAddScreen
import com.laimburggasse.aquariumcontroller.controller.ControllerEditScreen
import com.laimburggasse.aquariumcontroller.controller.ControllerProfileDatabase
import com.laimburggasse.aquariumcontroller.controller.ControllerSelectionScreen
import com.laimburggasse.aquariumcontroller.controller.ControllerViewModel
import com.laimburggasse.aquariumcontroller.feed.FeedProfileViewModel
import com.laimburggasse.aquariumcontroller.feed.FeedScreen
import com.laimburggasse.aquariumcontroller.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontroller.feed.edit.FeedProfileEditScreen
import com.laimburggasse.aquariumcontroller.feed.history.FeedHistoryScreen
import com.laimburggasse.aquariumcontroller.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontroller.heating.HeatingScreen
import com.laimburggasse.aquariumcontroller.info.InfoScreen
import com.laimburggasse.aquariumcontroller.overview.OverviewScreen
import com.laimburggasse.aquariumcontroller.refill.RefillScreen
import com.laimburggasse.aquariumcontroller.refill.history.RefillHistoryScreen
import com.laimburggasse.aquariumcontroller.schedule.ScheduleScreen
import com.laimburggasse.aquariumcontroller.timedata.TimeDataScreen
import com.laimburggasse.aquariumcontroller.ventilation.VentilationScreen

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

    private val feedProfileViewModel by viewModels<FeedProfileViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FeedProfileViewModel() as T
                }
            }
        }
    )

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
                    composable(ViewNavigationRoutes.controllerSelection)
                    {
                        ControllerSelectionScreen(
                            navHostController = navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.controllerAdd)
                    {
                        ControllerAddScreen(
                            navHostController = navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.controllerEdit)
                    {
                        ControllerEditScreen(
                            navHostController = navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.feed) {
                        FeedScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            myFeedProfileViewModel = feedProfileViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.feedHistory24h) {
                        FeedHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.feedHistory7d) {
                        FeedHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.feedEdit) {
                        FeedProfileEditScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            feedProfile = mainViewModel.getMySelectedFeedProfile()!!
                        )
                    }
                    composable(ViewNavigationRoutes.feedAddProfile) {
                        FeedProfileAddScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                            myFeedProfileViewModel = feedProfileViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.feedSchedule) {
                        FeedProfileScheduleScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel,
                        )
                    }
                    composable(ViewNavigationRoutes.balling) {
                        BallingScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.ballingHistory24h) {
                        BallingHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.ballingHistory7d) {
                        BallingHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.ballingEdit) {
                        if (mainViewModel.getMySelectedBallingProfile() != null) {
                            BallingProfileEditScreen(
                                navHostController = navHostController,
                                ballingProfile = mainViewModel.getMySelectedBallingProfile()!!,
                                mainViewModel = mainViewModel
                            )
                        }
                    }
                    composable(ViewNavigationRoutes.refill) {
                        RefillScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.refillHistory24h) {
                        RefillHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = false,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.refillHistory7d) {
                        RefillHistoryScreen(
                            navHostController = navHostController,
                            showFullWeek = true,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.heating) {
                        HeatingScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.ventilation) {
                        VentilationScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.timeData) {
                        TimeDataScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.schedule) {
                        ScheduleScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.info) {
                        InfoScreen(
                            navHostController = navHostController,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(ViewNavigationRoutes.overView) {
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

