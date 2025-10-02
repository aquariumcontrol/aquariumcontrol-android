package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditMenuComposablePreview

@Suppress("unused")
class FeedProfileEditMenuComposableTest {
    // @PreviewTest
    @Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedProfileEditMenuComposableSnapshotTest(){
        FeedProfileEditMenuComposablePreview()
    }
}