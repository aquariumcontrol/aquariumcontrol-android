package com.example.aquariumcontroller

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.aquariumcontroller.balling.BallingHistoryScreen
import com.example.aquariumcontroller.balling.BallingScreen
import com.example.aquariumcontroller.controller.ControllerAddScreen
import com.example.aquariumcontroller.controller.ControllerEditScreen
import com.example.aquariumcontroller.controller.ControllerProfileDatabase
import com.example.aquariumcontroller.controller.ControllerSelectionScreen
import com.example.aquariumcontroller.controller.ControllerViewModel
import com.example.aquariumcontroller.overview.OverviewScreen
import com.example.aquariumcontroller.refill.RefillHistoryScreen
import com.example.aquariumcontroller.refill.RefillScreen

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ControllerProfileDatabase::class.java,
            "controller_profiles.db"
        ).build()
    }

    private val controllerViewModel by viewModels<ControllerViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ControllerViewModel(db.controllerProfileDao()) as T
                }
            }
        }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navHostController = rememberNavController()
            val state by controllerViewModel.state.collectAsState()
            NavHost(
                navController = navHostController,
                startDestination = ViewNavigationRoutes.controllerSelection,
                builder = {
                    composable(ViewNavigationRoutes.controllerSelection)
                    {
                        ControllerSelectionScreen(navHostController, state = state, onEvent = controllerViewModel::onEvent)
                    }
                    composable(ViewNavigationRoutes.controllerAdd)
                    {
                        ControllerAddScreen(navHostController, state = state, onEvent = controllerViewModel::onEvent)
                    }
                    composable(ViewNavigationRoutes.controllerEdit)
                    {
                        ControllerEditScreen(
                            navHostController,
                            state = state,
                            onEvent = controllerViewModel::onEvent
                        )
                    }
                    composable(ViewNavigationRoutes.feed) {
                        FeedScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.balling) {
                        BallingScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.ballingHistory24h) {
                        BallingHistoryScreen(navHostController, false, null)
                    }
                    composable(ViewNavigationRoutes.ballingHistory7d) {
                        BallingHistoryScreen(navHostController, true, null)
                    }
                    composable(ViewNavigationRoutes.refill) {
                        RefillScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.refillHistory24h) {
                        RefillHistoryScreen(navHostController, false, null)
                    }
                    composable(ViewNavigationRoutes.refillHistory7d) {
                        RefillHistoryScreen(navHostController, true, null)
                    }
                    composable(ViewNavigationRoutes.heating) {
                        HeatingScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.ventilation) {
                        VentilationScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.timeData) {
                        TimeDataScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.schedule) {
                        ScheduleScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.info) {
                        InfoScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.overView) {
                        OverviewScreen(navHostController, null, null)
                    }
                }
            )
        }
    }
}

