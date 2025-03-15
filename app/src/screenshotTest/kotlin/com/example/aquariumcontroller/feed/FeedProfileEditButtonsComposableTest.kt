package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.feed.edit.FeedProfileEditButtonsComposablePortraitPreview

@Suppress("unused")
class FeedProfileEditButtonsComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun FeedProfileEditButtonsComposablePortraitSnapshotTest() {
        FeedProfileEditButtonsComposablePortraitPreview()
    }
}