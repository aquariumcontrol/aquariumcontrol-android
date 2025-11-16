package com.laimburggasse.aquariumcontrol.common.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun MainScaffold(
    isControllerLoaded: Boolean,
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
                    isControllerLoaded,
                    onNavigate
                )
            }
        }
    }
}