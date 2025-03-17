package com.laimburggasse.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontroller.feed.edit.FeedProfileEditMenuComposablePreview

@Suppress("unused")
class FeedProfileEditMenuComposableTest {
    @Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedProfileEditMenuComposableSnapshotTest(){
        FeedProfileEditMenuComposablePreview()
    }
}