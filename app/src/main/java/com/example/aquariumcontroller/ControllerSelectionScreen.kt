package com.example.aquariumcontroller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerSelectionScreen(navHostController: NavHostController?) {

    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory
    )

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navHostController?.navigate(ViewNavigationRoutes.controllerAdd)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create New Profile"
                        )
                    }
                }
            ) { innerPadding->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    MainDropdownMenuScreen(
                        navHostController,
                        ViewNavigationRoutes.controllerSelection
                    )
                    Text(
                        text = stringResource(id = R.string.ListOfControllers),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    // @todo: Replace with your LazyColumn implementation for the RecyclerView
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .wrapContentSize()
                    ) {
                        // Your items here
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.DefaultServerProfileTitle),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.DefaultServerProfileContent),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    // @todo: remove the button:
                    Button(onClick = { viewModel.controllerIsLoaded = true }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControllerSelectionViewPreview() {
    AquariumControllerTheme {
        ControllerSelectionScreen(null)
    }
}