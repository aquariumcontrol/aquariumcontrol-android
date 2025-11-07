package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileDisplayComposablePreview

@Suppress("unused")
class FeedProfileDisplayComposableTest {
    //@PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun FeedProfileDisplayComposableSnapshotTest() {
        FeedProfileDisplayComposablePreview()
    }
}