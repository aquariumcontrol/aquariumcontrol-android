package com.laimburggasse.aquariumcontrol.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun MainDropdownMenuComposable(
    navigationRouteSource: String,
    mainViewModel: MainViewModel? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    val isControllerLoaded: Boolean = mainViewModel?.isControllerLoaded() == true

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more_options)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            MainDropDownMenuItemOverviewComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemDisplayDataComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemBallingComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemRefillComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemHeatingComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemVentilationComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemScheduleComposable(
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            HorizontalDivider()
            MainDropDownMenuItemControllerSelectionComposable(
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
            MainDropDownMenuItemInfoComposable(
                navigationRouteSource = navigationRouteSource,
                onNavigate
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuComposablePreview() {
    // Preview consists of drop down menu items instead of complete menu,
    // because complete menu is not displayed correctly.
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            Column {
                MainDropDownMenuItemOverviewComposable(
                    isControllerLoaded = true,
                    onNavigate = {}
                )
                MainDropDownMenuItemDisplayDataComposable(isControllerLoaded = true, onNavigate = {})
                MainDropDownMenuItemBallingComposable(isControllerLoaded = true, onNavigate = {})
                MainDropDownMenuItemRefillComposable(isControllerLoaded = true, onNavigate = {})
                MainDropDownMenuItemFeedComposable(isControllerLoaded = true, onNavigate = {})
                MainDropDownMenuItemHeatingComposable(isControllerLoaded = true, onNavigate = {})
                MainDropDownMenuItemVentilationComposable(isControllerLoaded = true, onNavigate = {})
                MainDropDownMenuItemScheduleComposable(isControllerLoaded = true, onNavigate = {})
                HorizontalDivider()
                MainDropDownMenuItemControllerSelectionComposable(onNavigate = {})
                MainDropDownMenuItemInfoComposable(onNavigate = {})
            }
        }
    }
}
