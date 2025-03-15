package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.feed.history.FeedHistoryEventComposablePreview

class FeedHistoryEventComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedHistoryEventComposableSnapshotTest() {
        FeedHistoryEventComposablePreview()
    }
}