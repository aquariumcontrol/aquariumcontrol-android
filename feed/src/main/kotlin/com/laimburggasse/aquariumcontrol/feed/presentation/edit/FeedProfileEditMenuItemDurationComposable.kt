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

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewData.mockFeedProfile
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewData.mockFeedProfileEditNumericMenuItem

@Composable
fun FeedProfileEditMenuItemDurationComposable(
    feedProfile: FeedProfile,
    feedProfileEditMenuItem: FeedProfileEditMenuItem,
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit
) {
    val phaseId: Int = mapMenuItemIdToPhaseId(id = feedProfileEditMenuItem.id)
    val currentDuration: Double = if (phaseId < feedProfile.feedProfilePhases.size) {
        feedProfile.feedProfilePhases[phaseId].duration
    } else {
        0.0 // Default/fallback value
    }

    // This composable displays
    // - text describing the phase
    // - text field for editing the duration of a phase
    // - slider for editing the duration of a phase

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Text(text = feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
            Spacer(Modifier.weight(weight = 1f))
        }
        NumericDoubleOutlinedTextFieldComposable(
            value = currentDuration,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    val newNumericValue:Double = newStringValue.toDouble()
                    onFeedProfileEditUiEvent(FeedProfileEditUiEvent.DurationChange(
                        menuItemId = feedProfileEditMenuItem.id,
                        newDuration = newNumericValue)
                    )
                }
            },
            label = stringResource(id = R.string.duration_in_seconds),
        )
        Slider(value = currentDuration.toFloat(),
            modifier = Modifier.padding(all = 16.dp),
            onValueChange = {
                onFeedProfileEditUiEvent(FeedProfileEditUiEvent.DurationChange(
                    menuItemId = feedProfileEditMenuItem.id,
                    newDuration = it.toDouble())
                )
            },
            valueRange = FeedProfileEditSliderRange.RANGE
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedProfileEditMenuItemDurationComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuItemDurationComposable(
                feedProfile = mockFeedProfile,
                feedProfileEditMenuItem = mockFeedProfileEditNumericMenuItem,
                onFeedProfileEditUiEvent = {},
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuItemDurationComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuItemDurationComposable(
                feedProfile = mockFeedProfile,
                feedProfileEditMenuItem = mockFeedProfileEditNumericMenuItem,
                onFeedProfileEditUiEvent = {},
            )
        }
    }
}