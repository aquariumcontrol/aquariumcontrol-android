package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.feed.edit.FeedProfileEditScreenPreview

@Suppress("unused")
class FeedProfileEditScreenTest {
    @Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=600dp,height=1100dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=600dp,height=1100dp,orientation=landscape"
    )
    @Composable
    fun FeedProfileEditScreenSnapshotTest() {
        FeedProfileEditScreenPreview()
    }
}