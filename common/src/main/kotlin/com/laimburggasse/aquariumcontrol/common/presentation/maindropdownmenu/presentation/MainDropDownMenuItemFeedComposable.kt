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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.common.R
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemFeedComposable(
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.feed)) },
        onClick = { onNavigate(MainDropDownMenuNavigationEvent.ToFeed) },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.RotateRight,
                contentDescription = stringResource(R.string.feed)
            )
        },
        enabled = isControllerLoaded &&
                    navigationRouteSource != ViewNavigationRoutes.feedHistory &&
                    navigationRouteSource != ViewNavigationRoutes.feedEdit &&
                    navigationRouteSource != ViewNavigationRoutes.feedAddProfile &&
                    navigationRouteSource != ViewNavigationRoutes.feedSchedule
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun MainDropdownMenuItemFeedEnabledDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = true,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemFeedEnabledLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = true,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun MainDropdownMenuItemFeedDisabledDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = false,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemFeedDisabledLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = false,
                onNavigate = {}
            )
        }
    }
}
