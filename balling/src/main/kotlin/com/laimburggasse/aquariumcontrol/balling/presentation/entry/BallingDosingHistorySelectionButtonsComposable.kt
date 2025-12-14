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

package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.BallingNavigationEvent
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun BallingDosingHistorySelectionButtonsComposable(
    modifier: Modifier? = Modifier,
    onNavigateFromBalling: (BallingNavigationEvent, Int?) -> Unit
) {
    // This composable contains content separated from BallingScreen to allow
    // dynamic placement depending on screen orientation:
    // - button for visualizing balling events in the last 24 hours
    // - button for visualizing balling events in the last 7 days
    // The arrangement of the buttons follows the device orientation.

    Button(
        onClick = { onNavigateFromBalling(BallingNavigationEvent.ToBallingHistory24h, null) },
        modifier = modifier ?: Modifier
    ) {
        Text(
            text = stringResource(id = R.string.view_balling_events_24h),
            fontSize = 16.sp,
        )
    }
    Spacer(modifier = Modifier.width(width = 10.dp))
    Button(
        onClick = { onNavigateFromBalling(BallingNavigationEvent.ToBallingHistory7d, null) },
        modifier = modifier ?: Modifier,
    ) {
        Text(
            text = stringResource(id = R.string.view_balling_events_7d),
            fontSize = 16.sp,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingDosingHistorySelectionButtonsPortraitComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onNavigateFromBalling = { ballingNavigationEvent, ballingPumpId -> }
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingDosingHistorySelectionButtonsPortraitComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onNavigateFromBalling = { ballingNavigationEvent, ballingPumpId -> }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingDosingHistorySelectionButtonsLandscapeComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier.padding(vertical = 8.dp).weight(weight = 0.5f),
                    onNavigateFromBalling = { ballingNavigationEvent, ballingPumpId -> }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingDosingHistorySelectionButtonsLandscapeComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                BallingDosingHistorySelectionButtonsComposable(
                    modifier = Modifier.padding(vertical = 8.dp).weight(weight = 0.5f),
                    onNavigateFromBalling = { ballingNavigationEvent, ballingPumpId -> }
                )
            }
        }
    }
}
