package com.example.aquariumcontroller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun TimeDataScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null
) {
    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                MainDropdownMenuScreen(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.timeData,
                    mainViewModel = mainViewModel
                )
                Text(text = "TimeData")
                Button(onClick = { }) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeDataViewPreview() {
    AquariumControllerTheme {
        TimeDataScreen()
    }
}