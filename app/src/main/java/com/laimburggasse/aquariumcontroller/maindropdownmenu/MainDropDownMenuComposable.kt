package com.laimburggasse.aquariumcontroller.maindropdownmenu

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
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontroller.MainViewModel
import com.laimburggasse.aquariumcontroller.R
import com.laimburggasse.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun MainDropdownMenuComposable(
    navHostController: NavHostController? = null,
    navigationRouteSource: String,
    mainViewModel: MainViewModel? = null
) {
    val isControllerLoaded = mainViewModel?.isControllerLoaded() == true

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
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemDisplayDataComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemBallingComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemRefillComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemFeedComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemHeatingComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemVentilationComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemScheduleComposable(
                navHostController = navHostController,
                isControllerLoaded = isControllerLoaded,
                navigationRouteSource = navigationRouteSource
            )
            HorizontalDivider()
            MainDropDownMenuItemControllerSelectionComposable(
                navHostController = navHostController,
                navigationRouteSource = navigationRouteSource
            )
            MainDropDownMenuItemInfoComposable(
                navHostController = navHostController,
                navigationRouteSource = navigationRouteSource
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuComposablePreview() {
    // Preview consists of drop down menu items instead of complete menu,
    // because complete menu is not displayed correctly.
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            Column {
                MainDropDownMenuItemOverviewComposable(isControllerLoaded = true)
                MainDropDownMenuItemDisplayDataComposable(isControllerLoaded = true)
                MainDropDownMenuItemBallingComposable(isControllerLoaded = true)
                MainDropDownMenuItemRefillComposable(isControllerLoaded = true)
                MainDropDownMenuItemFeedComposable(isControllerLoaded = true)
                MainDropDownMenuItemHeatingComposable(isControllerLoaded = true)
                MainDropDownMenuItemVentilationComposable(isControllerLoaded = true)
                MainDropDownMenuItemScheduleComposable(isControllerLoaded = true)
                HorizontalDivider()
                MainDropDownMenuItemControllerSelectionComposable()
                MainDropDownMenuItemInfoComposable()
            }
        }
    }
}
