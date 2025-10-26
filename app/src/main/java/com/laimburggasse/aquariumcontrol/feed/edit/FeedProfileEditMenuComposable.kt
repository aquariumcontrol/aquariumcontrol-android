package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
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
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuComposable(
    itemsToRender: List<FeedProfileEditMenuItem>,
    feedProfile: FeedProfile,
    isExpanded: Set<Int>,
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit
) {
    // This composable acts as container for the feed profile edit menu items.
    if (LocalInspectionMode.current) {
        // In Preview: Render a simple, static Column instead of LazyColumn
        Column(modifier = Modifier.heightIn(max = 20000.dp)) {
            itemsToRender.forEach { feedProfileEditMenuItem ->
                FeedProfileEditMenuItemComposable(
                    feedProfileEditMenuItem,
                    isExpanded,
                    feedProfile,
                    onFeedProfileEditUiEvent
                )
            }
        }
    } else {
        // In App Runtime: Use the performant LazyColumn
        LazyColumn(modifier = Modifier.heightIn(max = 20000.dp)) {
            items(items = itemsToRender) { feedProfileEditMenuItem ->
                FeedProfileEditMenuItemComposable(
                    feedProfileEditMenuItem,
                    isExpanded,
                    feedProfile,
                    onFeedProfileEditUiEvent
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuComposablePreview() {
    val mockInitialExpandedItemIds: Set<Int> = setOf(1)

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileEditMenuComposable(
                itemsToRender = FeedProfileEditMenuItems.phases,
                feedProfile = mockFeedProfile,
                isExpanded = mockInitialExpandedItemIds,
                onFeedProfileEditUiEvent = {}
            )
        }
    }
}