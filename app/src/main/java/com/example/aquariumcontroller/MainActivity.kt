package com.example.aquariumcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = ViewNavigationRoutes.controllerSelection,
                builder = {
                    composable(ViewNavigationRoutes.controllerSelection)
                    {
                        ControllerSelectionScreen(navHostController)
                    }
                    composable(ViewNavigationRoutes.controllerAdd)
                    {
                        ControllerAddScreen(navHostController)
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

