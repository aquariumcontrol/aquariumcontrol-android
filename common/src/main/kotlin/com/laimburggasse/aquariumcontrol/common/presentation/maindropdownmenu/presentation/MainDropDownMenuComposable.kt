/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation

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
import com.laimburggasse.aquariumcontrol.common.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun MainDropdownMenuComposable(
    navigationRouteSource: String,
    isControllerLoaded: Boolean,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(value = false) }

    Box(modifier = Modifier.padding(all = 16.dp).fillMaxWidth().wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
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
@Composable
internal fun MainDropdownMenuComposableDarkPreview() {
    // Preview consists of drop down menu items instead of complete menu,
    // because complete menu is not displayed correctly.
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                MainDropDownMenuItemOverviewComposable(isControllerLoaded = true,onNavigate = {})
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuComposableLightPreview() {
    // Preview consists of drop down menu items instead of complete menu,
    // because complete menu is not displayed correctly.
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                MainDropDownMenuItemOverviewComposable(isControllerLoaded = true,onNavigate = {})
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
