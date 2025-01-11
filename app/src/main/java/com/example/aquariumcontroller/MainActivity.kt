package com.example.aquariumcontroller

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
import com.example.aquariumcontroller.serverprofile.ControllerProfileDatabase

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
                    composable(ViewNavigationRoutes.refill) {
                        RefillScreen(navHostController)
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
                }
            )
        }
    }
}

