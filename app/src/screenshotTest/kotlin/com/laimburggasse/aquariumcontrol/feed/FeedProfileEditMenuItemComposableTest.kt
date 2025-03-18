package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditMenuItemComposablePreview

@Suppress("unused")
class FeedProfileEditMenuItemComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedProfileEditMenuItemComposableSnapshotTest() {
        FeedProfileEditMenuItemComposablePreview()
    }
}