package com.laimburggasse.aquariumcontrol

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun MainScaffold(
    // It accepts the lean MainViewModel to control the menu
    mainViewModel: MainViewModel,
    navigationRouteSource: String,
    // The content of the specific screen is passed in as a lambda
    screenContent: @Composable () -> Unit,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    AquariumControllerTheme { // Theme can be applied here
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                // The screen's content is rendered here
                screenContent()

                // The scaffold OWNS the dropdown menu.
                // No screen needs to know about it.
                MainDropdownMenuComposable(
                    navigationRouteSource,
                    mainViewModel,
                    onNavigate
                )
            }
        }
    }
}