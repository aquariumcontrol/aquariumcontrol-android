package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.balling.history.BallingHistoryEventComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuComposable(
    feedProfileEditMenuItems: List<FeedProfileEditMenuItem>,
    feedProfileEditViewModel: FeedProfileEditViewModel? = null) {
    // This composable acts as container for the feed profile edit menu items.
    if (LocalInspectionMode.current) {
        // In Preview: Render a simple, static Column instead of LazyColumn
        Column(modifier = Modifier.heightIn(max = 20000.dp)) {
            feedProfileEditMenuItems.forEach { feedProfileEditMenuItem ->
                FeedProfileEditMenuItemComposable(feedProfileEditMenuItem)
            }
        }
    } else {
        // In App Runtime: Use the performant LazyColumn
        LazyColumn(modifier = Modifier.heightIn(max = 20000.dp)) {
            items(feedProfileEditMenuItems) { feedProfileEditMenuItem ->
                FeedProfileEditMenuItemComposable(feedProfileEditMenuItem, feedProfileEditViewModel)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuComposablePreview() {
    val mockFeedProfileEditMenuItemDuration = FeedProfileEditMenuItem(
        id = 0,
        level = 3,
        text = "Duration",
        isToggleButton = false,
        isNumericInput = true,
        children = emptyList(),
    )
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
            mockFeedProfileEditMenuItemDuration,
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
            mockFeedProfileEditMenuItemDuration,
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
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileEditMenuComposable(
                listOf(
                    mockFeedProfileEditMenuItemPhase1,
                    mockFeedProfileEditMenuItemPhase2
                )
            )
        }
    }
}