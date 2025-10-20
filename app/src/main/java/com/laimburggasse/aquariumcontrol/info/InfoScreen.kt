package com.laimburggasse.aquariumcontrol.info

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.HyperlinkTextComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun InfoScreen(
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    // This screen displays information about the app and the developer.

    AquariumControllerTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
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
                    HyperlinkTextComposable(
                        text = "Developer documentation",
                        url = "http://217.79.180.177/mediawiki/index.php/Main_Page",
                        textAlign = TextAlign.Center
                    )
                    HyperlinkTextComposable(
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
                    navigationRouteSource = ViewNavigationRoutes.info,
                    mainViewModel = mainViewModel,
                    onNavigate
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun InfoScreenPreview() {
    InfoScreen(onNavigate = {})
}