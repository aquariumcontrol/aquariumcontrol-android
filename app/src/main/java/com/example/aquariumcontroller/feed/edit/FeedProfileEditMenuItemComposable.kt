package com.example.aquariumcontroller.feed.edit

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquariumcontroller.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuItemComposable(feedProfileEditMenuItem: FeedProfileEditMenuItem,
                                      feedProfileEditViewModel: FeedProfileEditViewModel? = null) {
    if (feedProfileEditViewModel == null) {
        Log.d("FeedProfileEditMenuItemComposable", "called without view model")
    }
    else {
        Log.d("FeedProfileEditMenuItemComposable", "called with view model")
    }
    Row(
        modifier = Modifier
            .clickable {
                if (feedProfileEditMenuItem.children.isNotEmpty()) {
                    feedProfileEditMenuItem.isExpanded.value = !feedProfileEditMenuItem.isExpanded.value
                }
            }
            .padding(start = (feedProfileEditMenuItem.level * 16).dp, top = 8.dp, bottom = 8.dp), // Indentation        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
        Spacer(Modifier.weight(1f))
        if (feedProfileEditMenuItem.children.isNotEmpty() && !feedProfileEditMenuItem.isExpanded.value) {
            // Icon to indicate expandability
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
        }
        if (feedProfileEditMenuItem.isToggleButton) {
            Switch(
                checked = feedProfileEditMenuItem.isChecked.value,
                onCheckedChange = {
                    feedProfileEditMenuItem.isChecked.value = it
                    if (feedProfileEditViewModel != null) {
                        when (feedProfileEditMenuItem.toggleButtonType) {
                            FeedProfileEditToggleButtonType.Skimmer -> {
                                feedProfileEditViewModel.updateSkimmer(feedProfileEditMenuItem.id, it)
                            }
                            FeedProfileEditToggleButtonType.MainPump1 -> {
                                feedProfileEditViewModel.updateMainPump1(feedProfileEditMenuItem.id, it)
                            }
                            FeedProfileEditToggleButtonType.MainPump2 -> {
                                feedProfileEditViewModel.updateMainPump2(feedProfileEditMenuItem.id, it)
                            }
                            FeedProfileEditToggleButtonType.AuxPump1 -> {
                                feedProfileEditViewModel.updateAuxPump1(feedProfileEditMenuItem.id, it)
                            }
                            FeedProfileEditToggleButtonType.AuxPump2 -> {
                                feedProfileEditViewModel.updateAuxPump2(feedProfileEditMenuItem.id, it)
                            }
                            null -> { /* do nothing */ }
                        }
                    }
                })
        }
        if (feedProfileEditMenuItem.isNumericInput) {
            // Numeric input field
            NumericDoubleOutlinedTextFieldComposable(
                value = feedProfileEditMenuItem.inputValue.value,
                onValueChange = { newStringValue ->
                    val newNumericValue = newStringValue.toDouble()
                    if (feedProfileEditViewModel == null) {
                        Log.d("FeedProfileEditMenuItemComposable", "no view model available")
                    }
                    else {
                        Log.d("FeedProfileEditMenuItemComposable", "setting model to $newNumericValue")
                    }
                    feedProfileEditMenuItem.inputValue.value = newStringValue.toDouble()
                    feedProfileEditViewModel?.updatePhaseDuration(
                        feedProfileEditMenuItem.id, newNumericValue
                    )
                },
                label = stringResource(id = R.string.DurationInSeconds),
                modifier = Modifier
                    .padding(top = 16.dp)
            )
        }
    }
    if (feedProfileEditMenuItem.isExpanded.value) {
        FeedProfileEditMenuComposable(
            feedProfileEditMenuItems = feedProfileEditMenuItem.children,
            feedProfileEditViewModel = feedProfileEditViewModel

        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeedProfileEditMenuItemPreviewComposable() {

    val mockFeedProfileEditMenuItem1 = FeedProfileEditMenuItem(
        id = 1,
        level = 1,
        text = "Preview",
        isToggleButton = true,
        children = emptyList(),
    )

    val mockFeedProfileEditMenuItem2 = FeedProfileEditMenuItem(
        id = 1,
        level = 2,
        text = "Preview",
        isToggleButton = true,
        children = listOf(mockFeedProfileEditMenuItem1),
    )

    AquariumControllerTheme {
        FeedProfileEditMenuItemComposable(
            feedProfileEditMenuItem = mockFeedProfileEditMenuItem2
        )
    }
}