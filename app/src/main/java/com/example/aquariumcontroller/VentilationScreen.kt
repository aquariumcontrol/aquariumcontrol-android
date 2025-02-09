package com.example.aquariumcontroller

import android.content.res.Configuration
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
fun VentilationScreen(
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
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.ventilation,
                    mainViewModel = mainViewModel
                )
                Text(text = "Ventilation")
                Button(onClick = { }) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VentilationViewPreview() {
    AquariumControllerTheme {
        VentilationScreen()
    }
}