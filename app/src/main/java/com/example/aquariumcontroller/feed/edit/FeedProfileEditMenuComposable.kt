package com.example.aquariumcontroller.feed.edit

import android.util.Log
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuComposable(
    feedProfileEditMenuItems: List<FeedProfileEditMenuItem>,
    feedProfileEditViewModel: FeedProfileEditViewModel? = null) {

    if (feedProfileEditViewModel == null) {
        Log.d("FeedProfileEditMenuComposable", "no view model available")
    }
    else {
        Log.d("FeedProfileEditMenuComposable", "called with view model ")
    }
    LazyColumn(modifier = Modifier.heightIn(max = 20000.dp)) {
        items(feedProfileEditMenuItems) { item ->
            FeedProfileEditMenuItemComposable(item, feedProfileEditViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedProfileEditMenuPreviewComposable() {

    val mockFeedProfileEditMenuItemSkimmer = FeedProfileEditMenuItem(
        id = 1,
        level = 3,
        text = "Skimmer",
        isToggleButton = true,
        children = emptyList(),
    )
    val mockFeedProfileEditMenuItemMainPump1 = FeedProfileEditMenuItem(
        id = 2,
        level = 3,
        text = "Main pump 1",
        isToggleButton = true,
        children = emptyList(),
    )
    val mockFeedProfileEditMenuItemMainPump2 = FeedProfileEditMenuItem(
        id = 3,
        level = 3,
        text = "Main pump 2",
        isToggleButton = true,
        children = emptyList(),
    )
    val mockFeedProfileEditMenuItemAuxPump1 = FeedProfileEditMenuItem(
        id = 4,
        level = 3,
        text = "Aux. pump 1",
        isToggleButton = true,
        children = emptyList(),
    )
    val mockFeedProfileEditMenuItemAuxPump2 = FeedProfileEditMenuItem(
        id = 5,
        level = 3,
        text = "Aux. pump 2",
        isToggleButton = true,
        children = emptyList(),
    )

    val mockFeedProfileEditMenuItemPause = FeedProfileEditMenuItem(
        id = 6,
        level = 2,
        text = "Pause",
        isToggleButton = false,
        children = listOf(
            mockFeedProfileEditMenuItemSkimmer,
            mockFeedProfileEditMenuItemMainPump1,
            mockFeedProfileEditMenuItemMainPump2,
            mockFeedProfileEditMenuItemAuxPump1,
            mockFeedProfileEditMenuItemAuxPump2
        )
    )

    val mockFeedProfileEditMenuItemFeed = FeedProfileEditMenuItem(
        id = 7,
        level = 2,
        text = "Feed",
        isToggleButton = false,
        children = listOf(
            mockFeedProfileEditMenuItemSkimmer,
            mockFeedProfileEditMenuItemMainPump1,
            mockFeedProfileEditMenuItemMainPump2,
            mockFeedProfileEditMenuItemAuxPump1,
            mockFeedProfileEditMenuItemAuxPump2
        )
    )

    val mockFeedProfileEditMenuItemPhase1 = FeedProfileEditMenuItem(
        id = 8,
        level = 1,
        text = "Phase 1",
        isToggleButton = false,
        children = listOf(
            mockFeedProfileEditMenuItemPause,
            mockFeedProfileEditMenuItemFeed
        )
    )

    val mockFeedProfileEditMenuItemPhase2 = FeedProfileEditMenuItem(
        id = 8,
        level = 1,
        text = "Phase 2",
        isToggleButton = false,
        children = listOf(
            mockFeedProfileEditMenuItemPause,
            mockFeedProfileEditMenuItemFeed
        ),
    )

    mockFeedProfileEditMenuItemPhase1.updateIsExpanded(true)
    mockFeedProfileEditMenuItemPhase2.updateIsExpanded(true)
    mockFeedProfileEditMenuItemPause.updateIsExpanded(true)

    AquariumControllerTheme {
        FeedProfileEditMenuComposable(
            listOf(
                mockFeedProfileEditMenuItemPhase1,
                mockFeedProfileEditMenuItemPhase2
            )
        )
    }
}