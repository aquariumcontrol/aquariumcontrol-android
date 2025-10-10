package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreenPreviewEmpty
import com.laimburggasse.aquariumcontrol.feed.history.FeedHistoryScreenPreviewSuccess

@Suppress("unused")
class FeedHistoryScreenTest {
    //@PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=600dp,height=400dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=600dp,height=400dp,orientation=landscape"
    )
    @Composable
    fun FeedHistoryScreenSnapshotTestSuccess() {
        FeedHistoryScreenPreviewSuccess()
    }

    //@PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun FeedHistoryScreenSnapshotTestEmpty() {
        FeedHistoryScreenPreviewEmpty()
    }
}