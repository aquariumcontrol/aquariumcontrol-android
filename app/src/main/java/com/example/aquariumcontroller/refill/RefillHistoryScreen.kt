package com.example.aquariumcontroller.refill

import androidx.collection.emptyLongSet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun RefillHistoryScreen(navHostController: NavHostController?, showFullWeek: Boolean) {
    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainDropdownMenuScreen(
                        navHostController,
                        ViewNavigationRoutes.refill
                    )
                    val headlineText: String =
                        if (showFullWeek) {
                            stringResource(R.string.RefillEventsLast7Days)
                        }
                        else {
                            stringResource(R.string.RefillEventsLast24h)
                        }
                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RefillHistoryPreview() {
    AquariumControllerTheme {
        RefillHistoryScreen(null, true)
    }
}