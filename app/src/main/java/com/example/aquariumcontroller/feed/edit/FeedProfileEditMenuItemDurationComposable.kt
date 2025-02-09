package com.example.aquariumcontroller.feed.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquariumcontroller.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.R

@Composable
fun FeedProfileEditMenuItemDurationComposable(
    feedProfileEditMenuItem: FeedProfileEditMenuItem,
    feedProfileEditViewModel: FeedProfileEditViewModel? = null
) {
    val durationInputValue by feedProfileEditMenuItem.inputValue.observeAsState(
        initial = feedProfileEditViewModel?.getPhaseDuration(feedProfileEditMenuItem.id) ?: 0.0
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row() {
            Text(feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
            Spacer(Modifier.weight(1f))
        }
        NumericDoubleOutlinedTextFieldComposable(
            value = durationInputValue,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    val newNumericValue = newStringValue.toDouble()
                    feedProfileEditMenuItem.updateInputValue(newValue = newNumericValue)
                    feedProfileEditViewModel?.updatePhaseDuration(
                        feedProfileEditMenuItem.id, newNumericValue
                    )
                }
            },
            label = stringResource(id = R.string.DurationInSeconds),
            modifier = Modifier
                .padding(top = 16.dp)
        )
        Slider(value = durationInputValue.toFloat(),
            modifier = Modifier.padding(16.dp),
            onValueChange = {
                feedProfileEditViewModel?.updatePhaseDuration(
                    feedProfileEditMenuItem.id, it.toDouble()
                )
            },
            valueRange = FeedProfileEditSliderRange.RANGE
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedProfileEditMenuItemDurationPreviewComposable() {
    val mockFeedProfileEditMenuItem = FeedProfileEditMenuItem(
        id = 1,
        level = 1,
        text = "Preview",
        isToggleButton = false,
        children = emptyList(),
        isNumericInput = true,
    )

    FeedProfileEditMenuItemDurationComposable(mockFeedProfileEditMenuItem)
}