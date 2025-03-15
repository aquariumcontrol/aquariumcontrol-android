package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntryComposablePreview

@Suppress("unused")
class FeedScheduleEntryComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedScheduleEntryComposableSnapshotTest() {
        FeedScheduleEntryComposablePreview()
    }
}