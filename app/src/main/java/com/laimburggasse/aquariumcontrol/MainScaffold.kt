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
    mainViewModel: MainViewModel,
    navigationRouteSource: String,
    screenContent: @Composable () -> Unit,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit,
    darkTheme: Boolean? = true
) {
    AquariumControllerTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box {
                screenContent()

                MainDropdownMenuComposable(
                    navigationRouteSource,
                    mainViewModel,
                    onNavigate
                )
            }
        }
    }
}