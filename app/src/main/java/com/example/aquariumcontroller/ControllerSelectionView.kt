package com.example.aquariumcontroller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerSelectionView(navHostController: NavHostController?) {

    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory
    )

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                MainDropdownMenu(
                    navHostController,
                    ViewNavigationRoutes.controllerSelection
                )
                Text(text = "Controller Selection")
                Button(onClick = { viewModel.controllerIsLoaded = true} ) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControllerSelectionViewPreview() {
    AquariumControllerTheme {
        ControllerSelectionView(null)
    }
}