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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfile
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfileEditNumericMenuItem
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockInitialExpandedItemIdsEmpty

@Composable
fun FeedProfileEditMenuItemComposable(
    feedProfileEditMenuItem: FeedProfileEditMenuItem,
    isExpanded: Set<Int>,
    feedProfile: FeedProfile,
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit
) {
    // conversion of unique ID of menu item to feed profile phase ID
    val phaseId: Int = mapMenuItemIdToPhaseId(id = feedProfileEditMenuItem.id)
    val isThisItemExpanded: Boolean = feedProfileEditMenuItem.id in isExpanded

    val isChecked: Boolean = if (phaseId < feedProfile.feedProfilePhases.size) {
        when (feedProfileEditMenuItem.toggleButtonType) {
            FeedProfileEditToggleButtonType.Skimmer -> feedProfile.feedProfilePhases[phaseId].skimmer
            FeedProfileEditToggleButtonType.MainPump1 -> feedProfile.feedProfilePhases[phaseId].mainPump1
            FeedProfileEditToggleButtonType.MainPump2 -> feedProfile.feedProfilePhases[phaseId].mainPump2
            FeedProfileEditToggleButtonType.AuxPump1 -> feedProfile.feedProfilePhases[phaseId].auxPump1
            FeedProfileEditToggleButtonType.AuxPump2 -> feedProfile.feedProfilePhases[phaseId].auxPump2
            null -> false // Not a toggle button
        }
    } else {
        false // Default value if phaseId is out of bounds
    }

    // This composable displays one item of the feed profile edit menu:
    // - text describing the actuator
    // - switch allowing to enable/disable the actuator in the phase of the feed profile
    Row(
        modifier = Modifier
            .clickable {
                if (feedProfileEditMenuItem.children.isNotEmpty()) {
                    onFeedProfileEditUiEvent(FeedProfileEditUiEvent.ExpandChildren(
                        menuItemId = feedProfileEditMenuItem.id)
                    )
                }
            }
            .padding(start = (feedProfileEditMenuItem.level * 16).dp, top = 8.dp, bottom = 8.dp),
    ) {
        if (feedProfileEditMenuItem.children.isNotEmpty()) {
            Text(text = feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
            Spacer(Modifier.weight(weight = 1f))
        }
        if (feedProfileEditMenuItem.isToggleButton) {
            Text(text = feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
            Spacer(Modifier.weight(weight = 1f))
            if (feedProfileEditMenuItem.children.isNotEmpty() && !isThisItemExpanded) {
                // Icon to indicate expandability
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options)
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    onFeedProfileEditUiEvent(FeedProfileEditUiEvent.SwitchToggle(
                        menuItemId = feedProfileEditMenuItem.id,
                        toggleButtonType = feedProfileEditMenuItem.toggleButtonType!!,
                        isChecked = it
                    ))
                })
        }
        if (feedProfileEditMenuItem.isNumericInput) {
            FeedProfileEditMenuItemDurationComposable(
                feedProfile,
                feedProfileEditMenuItem,
                onFeedProfileEditUiEvent
            )
        }
    }
    if (isThisItemExpanded) {
        FeedProfileEditMenuComposable(
            itemsToRender = feedProfileEditMenuItem.children,
            feedProfile,
            isExpanded,
            onFeedProfileEditUiEvent
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuItemComposablePreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuItemComposable(
                feedProfileEditMenuItem = mockFeedProfileEditNumericMenuItem,
                feedProfile = mockFeedProfile,
                onFeedProfileEditUiEvent = {},
                isExpanded = mockInitialExpandedItemIdsEmpty,
            )
        }
    }
}