package com.example.aquariumcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelStore
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
                        ControllerSelectionView(navHostController)
                    }
                    composable(ViewNavigationRoutes.feed) {
                        FeedView(navHostController)
                    }
                    composable(ViewNavigationRoutes.balling) {
                        BallingView(navHostController)
                    }
                    composable(ViewNavigationRoutes.refill) {
                        RefillView(navHostController)
                    }
                    composable(ViewNavigationRoutes.heating) {
                        HeatingView(navHostController)
                    }
                    composable(ViewNavigationRoutes.ventilation) {
                        VentilationView(navHostController)
                    }
                    composable(ViewNavigationRoutes.timeData) {
                        TimeDataView(navHostController)
                    }
                    composable(ViewNavigationRoutes.schedule) {
                        ScheduleView(navHostController)
                    }
                    composable(ViewNavigationRoutes.info) {
                        InfoView(navHostController)
                    }
                }
            )
        }
    }
}

