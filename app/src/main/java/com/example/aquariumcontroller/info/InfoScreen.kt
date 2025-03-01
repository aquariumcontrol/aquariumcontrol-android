package com.example.aquariumcontroller.info

import HyperlinkText
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun InfoScreen(navHostController: NavHostController? = null, mainViewModel: MainViewModel? = null) {
    // This screen displays information about the app and the developer.

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = "Info",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = "Aquarium Control is an open source control system for marine fish tanks.",
                        textAlign = TextAlign.Center
                    )
                    HyperlinkText(
                        text = "Developer documentation",
                        url = "http://217.79.180.177/mediawiki/index.php/Main_Page",
                        textAlign = TextAlign.Center
                    )
                    HyperlinkText(
                        text = "Developer information",
                        url = "mailto:uwe.martin@gmail.com",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "This software comes with no warranties. Usage at your own risk.",
                        textAlign = TextAlign.Center
                    )
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.info,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InfoViewPreview() {
    InfoScreen()
}