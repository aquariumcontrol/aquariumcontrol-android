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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

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
            textAlign = TextAlign.Center
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

val mockFeedProfileEditMenuItem = FeedProfileEditMenuItem(
    id = 100,
    level = 3,
    text = "Preview",
    isToggleButton = false,
    children = emptyList(),
    isNumericInput = true,
    phaseType = FeedProfileEditPhaseType.FEED,
)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuItemDurationComposablePreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuItemDurationComposable(
                feedProfile = mockFeedProfile,
                feedProfileEditMenuItem = mockFeedProfileEditMenuItem,
                onFeedProfileEditUiEvent = {},
            )
        }
    }
}