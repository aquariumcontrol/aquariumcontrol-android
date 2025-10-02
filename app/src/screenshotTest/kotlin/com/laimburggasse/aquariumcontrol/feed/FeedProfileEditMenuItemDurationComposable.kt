package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditMenuItemDurationComposablePreview

@Suppress("unused")
class FeedProfileEditMenuItemDurationComposable {
    // @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedProfileEditMenuItemDurationComposableSnapshotTest() {
        FeedProfileEditMenuItemDurationComposablePreview()
    }
}