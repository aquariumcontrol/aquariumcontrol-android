package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditButtonsComposablePortraitPreview

@Suppress("unused")
class FeedProfileEditButtonsComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedProfileEditButtonsComposablePortraitSnapshotTest() {
        FeedProfileEditButtonsComposablePortraitPreview()
    }
}